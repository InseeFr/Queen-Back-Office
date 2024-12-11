package fr.insee.queen.infrastructure.db.configuration;

import fr.insee.queen.infrastructure.db.surveyunit.CipherProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataFactoryConfiguration {
    private final CipherProperties cipherProperties;

    @Bean
    public DataFactory dataFactory() {
        return new DataFactory(cipherProperties.enabled());
    }
}