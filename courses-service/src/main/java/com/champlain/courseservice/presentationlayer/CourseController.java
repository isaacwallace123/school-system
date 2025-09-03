package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.exceptionhandling.ApplicationExceptions;
import com.champlain.courseservice.mapper.EntityModelMapper;
import com.champlain.courseservice.validation.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CourseResponseModel> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping(value = "{courseid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> getCourseByCourseId(@PathVariable String courseid) {
        return Mono.just(courseid)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(ApplicationExceptions.invalidCourseId(courseid))
                .flatMap(courseService::getCourseBuCourseId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(ApplicationExceptions.courseNotFound(courseid));
    }

    @PostMapping()
    public Mono<ResponseEntity<CourseResponseModel>> addCourse(@RequestBody Mono<CourseRequestModel> courseRequestModel) {
        return courseRequestModel.transform(RequestValidator.validateBody())
                .as(courseService::addCourse)
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c));
    }

    @PutMapping(value = "{courseid}")
    public Mono<ResponseEntity<CourseResponseModel>> updateCourse(@RequestBody Mono<CourseRequestModel> courseRequestModel, @PathVariable String courseid) {
        return Mono.just(courseid)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(ApplicationExceptions.invalidCourseId(courseid))
                .thenReturn(courseRequestModel.transform(RequestValidator.validateBody()))
                .flatMap(validRequest -> courseService.updateCourse(validRequest, courseid))
                .map(ResponseEntity::ok)
                .switchIfEmpty(ApplicationExceptions.courseNotFound(courseid));
    }

    @DeleteMapping(value = "{courseid}")
    public Mono<ResponseEntity<CourseResponseModel>> deleteCourse(@PathVariable String courseid) {
        return Mono.just(courseid)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(ApplicationExceptions.invalidCourseId(courseid))
                .flatMap(courseService::deleteCourse)
                .map(ResponseEntity::ok)
                .switchIfEmpty(ApplicationExceptions.courseNotFound(courseid));
    }
}
