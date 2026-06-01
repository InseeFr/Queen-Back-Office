package fr.insee.queen.application.configuration;

import org.hibernate.cfg.MappingSettings;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Temporary class since Spring Boot 4 doesn't support mapping of some type like JsonNode or JsonArray by Jackson 3
 * for Hibernate. This feature is planned for a future release of Spring. Until then, this configuration class
 * does the job.
 */
@Configuration
public class HibernateJackson3Configuration {
    @Bean
    HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties ->
                hibernateProperties.put(MappingSettings.JSON_FORMAT_MAPPER,
                        Jackson3JsonFormatMapper.class.getName());
    }
}
