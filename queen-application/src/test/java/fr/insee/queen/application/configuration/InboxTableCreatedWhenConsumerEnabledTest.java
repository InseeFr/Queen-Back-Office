package fr.insee.queen.application.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify that the inbox table IS created
 * when feature.cross-environment-communication.consumer is set to true.
 */
@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        "feature.cross-environment-communication.emitter=false",
        "feature.cross-environment-communication.consumer=true"
})
class InboxTableCreatedWhenConsumerEnabledTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:14.15")
    )
            .withDatabaseName("queen_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Disable Docker Compose for tests
        registry.add("spring.docker.compose.enabled", () -> "false");

        // Configure PostgreSQL from Testcontainer
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        // Liquibase configuration
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:db/master.xml");

        // JPA configuration
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Test
    void shouldCreateInboxTable() {
        // When consumer is enabled, the inbox table should exist
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'inbox'",
            Integer.class
        );
        assertThat(count).isEqualTo(1);
    }
}