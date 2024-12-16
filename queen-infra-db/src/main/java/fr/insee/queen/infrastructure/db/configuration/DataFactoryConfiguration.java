package fr.insee.queen.infrastructure.db.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataFactoryConfiguration {
    private final CipherProperties cipherProperties;

    @Bean
    public DataFactory dataFactory() {
        return new DataFactory(cipherProperties.isEnabled());
    }
}