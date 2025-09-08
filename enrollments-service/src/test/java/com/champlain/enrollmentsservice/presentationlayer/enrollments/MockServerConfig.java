package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockServerConfig {
    // Start a MockServer instance and make it available as a ClientServer bean

    @Bean
    public ClientAndServer mockServer() {
        return ClientAndServer.startClientAndServer(1080); // Or anything that isn't being used frfr
    }

    // Create a MockServerClient
    @Bean
    public MockServerClient mockServerClient(ClientAndServer mockServer) {
        return new MockServerClient("localhost", mockServer.getPort());
    }
}
