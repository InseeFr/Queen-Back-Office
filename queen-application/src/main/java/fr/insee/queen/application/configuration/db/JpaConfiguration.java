package fr.insee.queen.application.configuration.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConditionalOnProperty(name = "feature.mongo.enabled", havingValue = "false")
@EnableTransactionManagement
@ComponentScan(basePackages = {"fr.insee.queen.infrastructure.db"})
@EnableJpaRepositories(basePackages = {"fr.insee.queen.infrastructure.db"})
@Slf4j
public class JpaConfiguration {
}
