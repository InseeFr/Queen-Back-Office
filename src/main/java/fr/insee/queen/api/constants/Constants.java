package fr.insee.queen.api.constants;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}

	public static final String ROLE_PREFIX = "ROLE_";
	// User
	public static final String GUEST = "GUEST";
	public static final String CAMPAIGN = "campaign";
	public static final String INTERVIEWER = "interviewer";
	public static final String REVIEWER = "reviewer";

	// Pilotage filter url
	public static final String API_HABILITATION = "/api/check-habilitation";
	public static final String API_PEARLJAM_SURVEYUNITS = "/api/survey-units";
	public static final String API_PEARLJAM_INTERVIEWER_CAMPAIGNS = "/api/interviewer/campaigns";

	// HealthCheck url
	public static final String API_HEALTH_CHECK = "/api/healthcheck";

	// Actuator url
	public static final String API_ACTUATOR = "/actuator/**";

}
