package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.exceptionhandling.exceptions.CourseNotFoundException;
import com.champlain.courseservice.exceptionhandling.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseControllerUnitTest {
    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    private final String EXISTING_COURSE_ID = "275c1138-0190-426e-94d4-4aaeb838acac";
    private final String NON_EXISTING_COURSE_ID = "275c1138-0190-426e-94d4-4aaeb838acad";
    private final String INVALID_COURSE_ID = "275c1138-0190-426e-94d4-4aaeb838acaca";

    @Test
    public void whenGetCourseByCourseId_withNonExistingCourseId_thenThrowCourseNotFoundException() {
        when(courseService.getCourseByCourseId(NON_EXISTING_COURSE_ID)).thenReturn(Mono.empty());

        Mono<ResponseEntity<CourseResponseModel>> result = courseController.getCourseByCourseId(NON_EXISTING_COURSE_ID);

        StepVerifier.create(result)
                .expectErrorMatches(except -> except instanceof CourseNotFoundException && except.getMessage().equals("Course with id=" + NON_EXISTING_COURSE_ID + " is not found"))
                .verify();
    }

    @Test
    public void whenAddCourse_withInvalidHours_thenThrowInvalidInputException() {
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-423",
                "Web Services Testing",
                0,
                3.0,
                "Computer Science"
        );

        Mono<ResponseEntity<CourseResponseModel>> result = courseController.addCourse(Mono.just(courseRequestModel));

        StepVerifier.create(result)
                .expectErrorMatches(except -> except instanceof InvalidInputException && except.getMessage().equals("Course hours must be greater than 0"))
                .verify();
    }

    @Test
    public void whenUpdateCourse_withInvalidHours_thenThrowInvalidInputException() {
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-423",
                "Web Services Testing",
                0,
                3.0,
                "Computer Science"
        );

        Mono<ResponseEntity<CourseResponseModel>> result = courseController.updateCourse(Mono.just(courseRequestModel), EXISTING_COURSE_ID);

        StepVerifier.create(result)
                .expectErrorMatches(except -> except instanceof InvalidInputException && except.getMessage().equals("Course hours must be greater than 0"))
                .verify();
    }
}