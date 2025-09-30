package fr.insee.queen.jms.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Setter
@Configuration
public class CustomProperties {

    @Value("${fr.insee.broker.name}")
    private String brokerName;

    @Value("${fr.insee.broker.pulsar.webServiceUrl}") // ou web-service-url selon ta clé
    private String webServiceUrl;

    @Value("${fr.insee.broker.pulsar.brokerServiceUrl}") // ou broker-service-url
    private String brokerServiceUrl;

    @Value("${fr.insee.broker.queue.interrogation.request}")
    private String queueInterrogationRequest;

    @Value("${fr.insee.broker.queue.interrogation.response}")
    private String queueInterrogationResponse;
}
