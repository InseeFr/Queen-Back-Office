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
 * Adds 'cross-env-consumer' context when consumer is enabled.
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
            StringBuilder contextsBuilder = new StringBuilder(currentContexts == null ? "" : currentContexts);

            if (properties.emitter()) {
                if (!contextsBuilder.isEmpty()) {
                    contextsBuilder.append(",");
                }
                contextsBuilder.append("cross-env-emitter");
            }

            if (properties.consumer()) {
                if (!contextsBuilder.isEmpty()) {
                    contextsBuilder.append(",");
                }
                contextsBuilder.append("cross-env-consumer");
            }

            String newContexts = contextsBuilder.toString();
            liquibase.setContexts(newContexts);
            log.info("Liquibase contexts configured: {} (emitter={}, consumer={})",
                newContexts, properties.emitter(), properties.consumer());
        }
        return bean;
    }
}
