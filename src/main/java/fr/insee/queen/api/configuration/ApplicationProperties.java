package fr.insee.queen.api.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
	public enum Mode {Basic, Keycloak, NoAuth};
	
	private Mode mode;
	private String crosOrigin;
	
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	public String getCrosOrigin() {
		return crosOrigin;
	}
	public void setCrosOrigin(String crosOrigin) {
		this.crosOrigin = crosOrigin;
	}
}
