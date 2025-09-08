package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.exceptionhandling.exceptions.CourseNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
}