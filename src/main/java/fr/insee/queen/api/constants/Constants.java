package fr.insee.queen.api.constants;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}

	public static final String ROLE_PREFIX = "ROLE_";
	// User
	public static final String GUEST = "GUEST";
	public static final String CAMPAIGN = "campaign";

	// HealthCheck url
	public static final String API_HEALTH_CHECK = "/api/healthcheck";

	// Actuator url
	public static final String API_ACTUATOR = "/actuator/**";

}
