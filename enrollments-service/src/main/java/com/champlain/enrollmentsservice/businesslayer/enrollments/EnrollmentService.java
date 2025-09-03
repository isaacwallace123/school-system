package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EnrollmentService {
    Flux<EnrollmentResponseModel> getEnrollments();
    Mono<EnrollmentResponseModel> getEnrollmentById(String enrollmentid);
    Mono<EnrollmentResponseModel> addEnrollment(Mono<EnrollmentRequestModel> enrollmentRequestModel);
    Mono<EnrollmentResponseModel> updateEnrollment(Mono<EnrollmentRequestModel> enrollmentRequestModel, String enrollmentid);
    Mono<EnrollmentResponseModel> deleteEnrollment(String enrollmentid);
}
