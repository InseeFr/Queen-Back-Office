package fr.insee.queen.jms.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ConfigurationProperties("fr.insee.mock.mockcreatecampaignandquestionnaire")
@Configuration
public class ConsumerProperties {

    @Value("${fr.insee.mock.mockcreatecampaignandquestionnaire}")
    private boolean mockcreatecampaignandquestionnaire;

}