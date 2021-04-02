package fr.insee.queen.api.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fr.insee.queen.application", ignoreUnknownFields = false)
public class ApplicationProperties {
	public enum Mode {basic, keycloak, noauth};
	public enum PersistenceType {JPA, MONGODB};
	
	private Mode mode;

	private PersistenceType persistenceType;
	private String crosOrigin;
	
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	public PersistenceType getPersistenceType() {
		return persistenceType;
	}
	public void setPersistenceType(PersistenceType persistenceType) {
		this.persistenceType = persistenceType;
	}
	public String getCrosOrigin() {
		return crosOrigin;
	}
	public void setCrosOrigin(String crosOrigin) {
		this.crosOrigin = crosOrigin;
	}
}
