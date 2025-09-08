package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.domainclientlayer.courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentResponseModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EnrollmentControllerIntegrationTest extends AbstractIntegrationClass {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    public void whenAddValidEnrollmentRequest_thenReturnEnrollmentResponseModel() {
        try {
            mockGetStudentByStudentIdSuccess(testData.student1ResponseModel);
            mockGetCourseByCourseIdSuccess(testData.course1ResponseModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        webTestClient.post()
                .uri("/api/v1/enrollments")
                .body(Mono.just(testData.enrollment1RequestModel), EnrollmentRequestModel.class)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertNotNull(enrollmentResponseModel.enrollmentId());
                    assertEquals(testData.enrollment1RequestModel.enrollmentYear(), enrollmentResponseModel.enrollmentYear());
                });

        StepVerifier.create(enrollmentRepository.count())
                .expectNext(testData.dbSize + 1)
                .verifyComplete();
    }

    private void mockGetCourseByCourseIdSuccess(CourseResponseModel courseResponseModel) throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(courseResponseModel);

        mockServerClient
                .when(HttpRequest.request("/api/v1/courses/" + courseResponseModel.courseId()))
                .respond(HttpResponse
                        .response(jsonBody)
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    private void mockGetStudentByStudentIdSuccess(StudentResponseModel studentResponseModel) throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(studentResponseModel);

        mockServerClient
                .when(HttpRequest.request("/api/v1/students/" + studentResponseModel.studentId()))
                .respond(HttpResponse
                        .response(jsonBody)
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                );
    }
}