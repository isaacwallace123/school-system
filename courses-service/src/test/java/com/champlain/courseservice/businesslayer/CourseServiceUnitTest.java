package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceUnitTest {
    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;

    Course course = Course.builder()
            .id(1)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-420")
            .courseName("Web Services")
            .numHours(48)
            .numCredits(3.0)
            .department("Computer Science")
            .build();

    Course secondCourse = Course.builder()
            .id(2)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-422")
            .courseName("Micro Services")
            .numHours(50)
            .numCredits(3.0)
            .department("Computer Science")
            .build();

    Course thirdCourse = Course.builder()
            .id(3)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-421")
            .courseName("Networking Servers")
            .numHours(31)
            .numCredits(2.0)
            .department("Computer Science")
            .build();

    @Test
    public void whenGetAllCourses_thenReturnThreeCourses() {
        when(courseRepository.findAll())
                .thenReturn(Flux.just(course, secondCourse, thirdCourse));

        Flux<CourseResponseModel> result = courseService.getAllCourses();

        StepVerifier.create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertEquals(courseResponseModel.courseNumber(), course.getCourseNumber());
                    return true;
                })
                .expectNextMatches(courseResponseModel -> courseResponseModel.courseNumber().equals(secondCourse.getCourseNumber()))
                .expectNextMatches(courseResponseModel -> courseResponseModel.courseNumber().equals(thirdCourse.getCourseNumber()))
                .verifyComplete();
    }
}