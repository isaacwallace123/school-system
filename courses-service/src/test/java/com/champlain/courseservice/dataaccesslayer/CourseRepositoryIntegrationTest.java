package com.champlain.courseservice.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@ActiveProfiles("test")
class CourseRepositoryIntegrationTest {
    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    public void setupDB() {
        StepVerifier
                .create(courseRepository.deleteAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void whenFindCourseByCourseId_withExistingCourseId_thenReturnCourse() {
        String courseId = UUID.randomUUID().toString();
        Course course = Course.builder()
                .courseId(courseId)
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(48)
                .numCredits(3.0)
                .department("Computer Science")
                .build();

        StepVerifier
                .create(courseRepository.save(course))
                .consumeNextWith(newCourse -> {
                    assertNotNull(newCourse);
                    assertEquals(course.getCourseId(), newCourse.getCourseId());
                })
                .verifyComplete();

        StepVerifier
                .create(courseRepository.findCourseByCourseId(courseId))
                .consumeNextWith(foundCourse -> {
                    assertNotNull(foundCourse);
                    assertEquals(course.getCourseId(), foundCourse.getCourseId());
                })
                .verifyComplete();
    }

    @Test
    public void whenFindCourseByCourseId_withNonExistingCourseId_thenReturnEmptyMono() {
        String nonExistingCourseId = UUID.randomUUID().toString();

        StepVerifier
                .create(courseRepository.findCourseByCourseId(nonExistingCourseId))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void whenSaveCourseWithDuplicateCourseId_thenThrowDuplicateKeyException() {
        String courseId = UUID.randomUUID().toString();
        Course course = Course.builder()
                .courseId(courseId)
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(48)
                .numCredits(3.0)
                .department("Computer Science")
                .build();

        Course secondCourse = Course.builder()
                .courseId(courseId)
                .courseNumber("cat-422")
                .courseName("Micro Services")
                .numHours(50)
                .numCredits(3.0)
                .department("Computer Science")
                .build();

        StepVerifier
                .create(courseRepository.save(course))
                .consumeNextWith(newCourse -> {
                    assertNotNull(newCourse);
                    assertEquals(course.getCourseId(), newCourse.getCourseId());
                })
                .verifyComplete();

        StepVerifier
                .create(courseRepository.save(secondCourse))
                .verifyError(DuplicateKeyException.class);
    }
}