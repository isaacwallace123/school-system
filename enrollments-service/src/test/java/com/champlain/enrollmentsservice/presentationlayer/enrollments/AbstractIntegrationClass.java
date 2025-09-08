package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.client.MockServerClient;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@MockServerTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.data.mongodb.port=0",
        "app.students-service.port=${mockServerPort}",
        "app.courses-service.port=${mockServerPort}"
})
public abstract class AbstractIntegrationClass {
    @Autowired
    protected MockServerClient mockServerClient;

    @Autowired
    protected WebTestClient webTestClient;

    protected final TestData testData = new TestData();

    @BeforeAll
    public static void setup() {
        ConfigurationProperties.disableLogging(false); // Make false for logging and debugging
    }
}
