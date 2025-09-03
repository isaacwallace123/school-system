package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentService;
import com.champlain.enrollmentsservice.exceptionhandling.ApplicationExceptions;
import com.champlain.enrollmentsservice.validation.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EnrollmentResponseModel> getAllEnrollments() {
        return enrollmentService.getEnrollments();
    }

    @GetMapping(value = "{enrollmentid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EnrollmentResponseModel>> getEnrollmentById(@PathVariable String enrollmentid) {
        return Mono.just(enrollmentid)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(ApplicationExceptions.invalidEnrollmentId(enrollmentid))
                .flatMap(enrollmentService::getEnrollmentById)
                .map(ResponseEntity::ok)
                .switchIfEmpty(ApplicationExceptions.enrollmentNotFound(enrollmentid));
    }

    @PostMapping()
    public Mono<ResponseEntity<EnrollmentResponseModel>> addEnrollment(@RequestBody Mono<EnrollmentRequestModel> enrollmentRequestModel) {
        return enrollmentRequestModel.transform(RequestValidator.validateBody())
                .as(enrollmentService::addEnrollment)
                .map(e -> ResponseEntity.status(HttpStatus.CREATED).body(e));
    }

    @PutMapping(value = "{enrollmentid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EnrollmentResponseModel>> updateEnrollment(@RequestBody Mono<EnrollmentRequestModel> enrollmentRequestModel, @PathVariable String enrollmentid) {
        return Mono.just(enrollmentid)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(ApplicationExceptions.invalidEnrollmentId(enrollmentid))
                .thenReturn(enrollmentRequestModel.transform(RequestValidator.validateBody()))
                .flatMap(validRequest -> enrollmentService.updateEnrollment(validRequest, enrollmentid))
                .map(ResponseEntity::ok)
                .switchIfEmpty(ApplicationExceptions.enrollmentNotFound(enrollmentid));
    }

    @DeleteMapping(value = "{enrollmentid}")
    public Mono<ResponseEntity<EnrollmentResponseModel>> deleteEnrollment(@PathVariable String enrollmentid) {
        return Mono.just(enrollmentid)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(ApplicationExceptions.invalidEnrollmentId(enrollmentid))
                .flatMap(enrollmentService::deleteEnrollment)
                .map(ResponseEntity::ok)
                .switchIfEmpty(ApplicationExceptions.enrollmentNotFound(enrollmentid));
    }
}
