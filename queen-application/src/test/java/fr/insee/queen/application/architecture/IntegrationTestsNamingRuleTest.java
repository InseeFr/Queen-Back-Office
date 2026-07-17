package fr.insee.queen.application.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class IntegrationTestsNamingRuleTest {
    @Test
    void spring_boot_tests_should_end_with_IT() {
        JavaClasses classes = new ClassFileImporter()
                .importPackages("fr.insee.queen");

        classes.stream()
                .filter(c -> c.isAnnotatedWith(
                        org.springframework.boot.test.context.SpringBootTest.class))
                .forEach(c ->
                        assertThat(c.getSimpleName())
                                .endsWith("IT")
                );
    }
}
