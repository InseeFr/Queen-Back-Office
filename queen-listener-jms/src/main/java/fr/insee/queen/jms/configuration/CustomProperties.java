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

    @Value("${broker.name}")
    private String brokerName;

    @Value("${broker.queue.interrogation.request}")
    private String queueInterrogationRequest;

    @Value("${broker.queue.interrogation.response}")
    private String queueInterrogationResponse;
}
