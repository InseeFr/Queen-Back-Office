package fr.insee.queen.jms.configuration;

import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.synchronisation.gateway.SynchronisationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockBeansConfiguration {

    @Bean
    @ConditionalOnMissingBean(SynchronisationRepository.class)
    public SynchronisationRepository synchronisationRepository() {
        return new SynchronisationRepository() {
            @Override
            public Interrogation synchronise(String interrogationId) {
                throw new UnsupportedOperationException("SynchronisationRepository not available in listener-jms context");
            }
        };
    }
}
