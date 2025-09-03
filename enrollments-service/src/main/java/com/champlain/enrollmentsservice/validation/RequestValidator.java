package com.champlain.enrollmentsservice.validation;

import com.champlain.enrollmentsservice.exceptionhandling.ApplicationExceptions;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RequestValidator {
    public static UnaryOperator<Mono<EnrollmentRequestModel>> validateBody() {
        return enrollmentRequestModel -> enrollmentRequestModel
                .filter(hasSemester())
                .switchIfEmpty(ApplicationExceptions.missingSemester())
                .filter(hasCourse())
                .switchIfEmpty(ApplicationExceptions.missingCourseId())
                .filter(haseStudent())
                .switchIfEmpty(ApplicationExceptions.missingStudentId());
    }

    private static Predicate<EnrollmentRequestModel> hasSemester() {
        return enrollmentRequestModel -> Objects.nonNull(enrollmentRequestModel.semester());
    }

    private static Predicate<EnrollmentRequestModel> hasCourse() {
        return enrollmentRequestModel -> Objects.nonNull(enrollmentRequestModel.courseId());
    }

    private static Predicate<EnrollmentRequestModel> haseStudent() {
        return enrollmentRequestModel -> Objects.nonNull(enrollmentRequestModel.studentId());
    }
}
