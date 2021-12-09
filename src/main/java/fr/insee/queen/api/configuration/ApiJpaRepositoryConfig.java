package fr.insee.queen.api.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EnableJpaRepositories(basePackages = "fr.insee.queen.api.repository")
public class ApiJpaRepositoryConfig {

}
