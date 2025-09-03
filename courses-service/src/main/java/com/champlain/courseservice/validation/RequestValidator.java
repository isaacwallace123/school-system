package com.champlain.courseservice.validation;

import com.champlain.courseservice.exceptionhandling.ApplicationExceptions;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RequestValidator {
    public static UnaryOperator<Mono<CourseRequestModel>> validateBody() {
        return courseRequestModel -> courseRequestModel
                .filter(hasCourseNumber())
                .switchIfEmpty(ApplicationExceptions.missingCourseNumber())
                .filter(hasCourseName())
                .switchIfEmpty(ApplicationExceptions.missingCourseName())
                .filter(hasCourseCredit())
                .switchIfEmpty(ApplicationExceptions.invalidCourseCredits())
                .filter(hasCourseHours())
                .switchIfEmpty(ApplicationExceptions.invalidCourseHours());
    }

    private static Predicate<CourseRequestModel> hasCourseNumber() {
        return courseRequestModel -> Objects.nonNull(courseRequestModel.courseNumber());
    }

    private static Predicate<CourseRequestModel> hasCourseName() {
        return courseRequestModel -> Objects.nonNull(courseRequestModel.courseName());
    }

    private static Predicate<CourseRequestModel> hasCourseCredit() {
        return courseRequestModel -> Objects.nonNull(courseRequestModel.numCredits()) && (courseRequestModel.numCredits() > 0);
    }

    private static Predicate<CourseRequestModel> hasCourseHours() {
        return courseRequestModel -> Objects.nonNull(courseRequestModel.numHours()) && (courseRequestModel.numHours() > 0);
    }
}
