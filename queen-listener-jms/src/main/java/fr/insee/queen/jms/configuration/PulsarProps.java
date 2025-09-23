package fr.insee.queen.jms.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pulsar")
@Getter
@Setter
public class PulsarProps {
  private String webServiceUrl;
  private String brokerServiceUrl;
  private String clientId;
  // getters/setters
}
