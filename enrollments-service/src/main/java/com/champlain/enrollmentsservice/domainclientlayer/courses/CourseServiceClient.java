package com.champlain.enrollmentsservice.domainclientlayer.courses;

import com.champlain.enrollmentsservice.exceptionhandling.ApplicationExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException.UnprocessableEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import reactor.core.publisher.Mono;

@Slf4j
public class CourseServiceClient {

    private final WebClient webClient;

    public CourseServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<CourseResponseModel> getCourseByCourseId(String courseid) {
        return webClient.get()
                .uri("/{courseid}", courseid)
                .retrieve()
                .bodyToMono(CourseResponseModel.class)
                .onErrorResume(NotFound.class, ex -> ApplicationExceptions.courseNotFound(courseid))
                .onErrorResume(UnprocessableEntity.class, ex -> ApplicationExceptions.invalidCourseId(courseid));
    }
}
