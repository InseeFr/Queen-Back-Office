package fr.insee.queen.application.configuration;

import fr.insee.queen.application.crossenvironmentcommunication.configuration.CrossEnvironmentCommunicationProperties;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Customizes Liquibase contexts based on feature flags.
 * Adds 'cross-env-emitter' context when emitter is enabled.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LiquibaseContextCustomizer implements BeanPostProcessor {

    private final CrossEnvironmentCommunicationProperties properties;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SpringLiquibase liquibase) {
            String currentContexts = liquibase.getContexts();

            if (properties.emitter()) {
                String newContexts = currentContexts == null || currentContexts.isEmpty()
                    ? "cross-env-emitter"
                    : currentContexts + ",cross-env-emitter";

                liquibase.setContexts(newContexts);
                log.info("Liquibase contexts configured: {}", newContexts);
            } else {
                log.info("Liquibase contexts configured: {} (cross-env-emitter disabled)", currentContexts);
            }
        }
        return bean;
    }
}
