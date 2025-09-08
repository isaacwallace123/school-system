package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.exceptionhandling.HttpErrorInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourseControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CourseRepository courseRepository;

    private final Long dbSize = 1000L;

    private String existingCourseId;
    private Course existingCourse;

    @BeforeEach
    public void setupDB() {
        StepVerifier
                .create(courseRepository.count())
                .consumeNextWith(count -> {
                    assertEquals(dbSize, count);
                })
                .verifyComplete();
    }

    @Test
    public void getAllCoursesEventStream() {
        webTestClient.get()
                .uri("/api/v1/courses")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(CourseResponseModel.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectNextCount(dbSize)
                .verifyComplete();
    }

    @Test
    public void whenGetCourseByCourseId_withExistingCourseId_thenReturnCourseResponseModel() {
        Mono.from(
            courseRepository.findAll()
                .take(1)
                .doOnNext(course -> {
                    existingCourse = course;
                    existingCourseId = course.getCourseId();
                })
            )
            .as(StepVerifier::create)
            .expectNextCount(1)
            .verifyComplete();

        webTestClient.get()
                .uri("/api/v1/courses/{courseid}", existingCourseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.courseId").isEqualTo(existingCourse.getCourseId());
    }

    @Test
    public void whenNewCourse_withValidRequestBody_shouldReturnSuccess() {
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-423",
                "Web Services Testing",
                45,
                3.0,
                "Computer Science"
        );

        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertNotNull(courseResponseModel.courseId());
                    //add the rest of the fields
                });
    }

    @Test
    public void whenAddNewCourse_withMissingCourseName_shouldReturnUnProcessableEntity() {
        var courseRequestModel = this.resourceToString("courseRequestModel-missing-courseName-422.json");

        webTestClient.post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertEquals("Course name is required", errorInfo.getMessage());
                });
    }

    protected String resourceToString(String relativePath) {
        final Path TEST_RESOURCES_PATH = Path.of("src/test/resources");

        try {
            return Files.readString(TEST_RESOURCES_PATH.resolve(relativePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}