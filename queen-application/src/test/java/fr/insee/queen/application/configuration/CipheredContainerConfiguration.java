package fr.insee.queen.application.configuration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test-cipher")
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public abstract class CipheredContainerConfiguration {

    private static final PostgreSQLContainer postgreSQLCipheredContainer;

    static {
        postgreSQLCipheredContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.1"));
        postgreSQLCipheredContainer.start(); //singleton container started once in this class and used by all inheriting test classes
    }

    @DynamicPropertySource
    private static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLCipheredContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLCipheredContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLCipheredContainer::getPassword);
    }
}