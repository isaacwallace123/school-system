package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.mapper.EntityModelMapper;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Flux<CourseResponseModel> getAllCourses() {
        return courseRepository.findAll().map(EntityModelMapper::toModel);
    }

    @Override
    public Mono<CourseResponseModel> getCourseByCourseId(String courseId) {
        return courseRepository.findCourseByCourseId(courseId).doOnNext(c -> log.debug("Course found has id: {}", c.getCourseId())).map(EntityModelMapper::toModel);
    }

    @Override
    public Mono<CourseResponseModel> addCourse(Mono<CourseRequestModel> courseRequestModel) {
        return courseRequestModel
                .map(EntityModelMapper::toEntity)
                .flatMap(courseRepository::save)
                .map(EntityModelMapper::toModel);
    }

    @Override
    public Mono<CourseResponseModel> updateCourse(Mono<CourseRequestModel> courseRequestModel, String courseId) {
        return courseRepository
                .findCourseByCourseId(courseId)
                .flatMap(found -> courseRequestModel
                        .map(EntityModelMapper::toEntity)
                        .doOnNext(e -> e.setCourseId(courseId))
                        .doOnNext(e -> e.setId(found.getId()))
                )
                .flatMap(courseRepository::save)
                .map(EntityModelMapper::toModel);
    }

    @Override
    public Mono<CourseResponseModel> deleteCourse(String courseId) {
        return courseRepository
                .findCourseByCourseId(courseId)
                .flatMap(found -> courseRepository
                        .delete(found)
                        .then(Mono.just(found))
                )
                .map(EntityModelMapper::toModel);
    }
}
