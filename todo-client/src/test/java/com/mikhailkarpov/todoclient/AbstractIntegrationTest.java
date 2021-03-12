package com.mikhailkarpov.todoclient;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIntegrationTest {

    static final KeycloakContainer keycloak;

    static {
        keycloak = new KeycloakContainer().withReuse(true);

        keycloak.start();
    }

    @DynamicPropertySource
    static void configIssuerUri(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                AbstractIntegrationTest::getIssuerUri);
    }

    static String getIssuerUri() {
        return String.format("http://%s:%d/auth/realms/master", keycloak.getHost(), keycloak.getFirstMappedPort());
    }
}
