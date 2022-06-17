package fr.insee.queen.api.constants;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}

	// User
	public static final String GUEST = "GUEST";
	public static final String CAMPAIGN = "campaign";
	public static final String AUTHORIZATION = "Authorization";
	public static final String INTERVIEWER = "interviewer";
	public static final String REVIEWER = "reviewer";

	// API url for endpoints
	public static final String API_CAMPAIGNS = "/api/campaigns";
	public static final String API_ADMIN_CAMPAIGNS = "/api/admin/campaigns";
	public static final String API_CAMPAIGN_ID = "/api/campaign/{id}";
	public static final String API_CAMPAIGN_CONTEXT = "/api/campaign/context";
	public static final String API_CAMPAIGN_ID_SURVEY_UNITS = "/api/campaign/{id}/survey-units";
	public static final String API_CAMPAIGN_ID_SURVEY_UNIT = "/api/campaign/{id}/survey-unit";
	public static final String API_CAMPAIGN_ID_METADATA = "/api/campaign/{id}/metadata";
	public static final String API_CAMPAIGN_ID_QUESTIONAIRES = "/api/campaign/{id}/questionnaires";
	public static final String API_CAMPAIGN_ID_QUESTIONAIREID = "/api/campaign/{id}/questionnaire-id";
	public static final String API_CAMPAIGN_ID_REQUIREDNOMENCLATURES = "/api/campaign/{id}/required-nomenclatures";
	public static final String API_SURVEYUNITS_STATEDATA = "/api/survey-units/state-data";
	public static final String API_SURVEYUNITS = "/api/survey-units";
	public static final String API_SURVEYUNIT_ID = "/api/survey-unit/{id}";
	public static final String API_SURVEYUNIT_ID_DATA = "/api/survey-unit/{id}/data";
	public static final String API_SURVEYUNIT_ID_COMMENT = "/api/survey-unit/{id}/comment";
	public static final String API_SURVEYUNIT_ID_STATEDATA = "/api/survey-unit/{id}/state-data";
	public static final String API_SURVEYUNIT_ID_DEPOSITPROOF = "/api/survey-unit/{id}/deposit-proof";
	public static final String API_SURVEYUNIT_ID_PERSONALIZATION = "/api/survey-unit/{id}/personalization";
	public static final String API_SURVEYUNIT_ID_TEMP_ZONE = "/api/survey-unit/{id}/temp-zone";
	public static final String API_SURVEYUNITS_TEMP_ZONE = "/api/survey-units/temp-zone";
	public static final String API_NOMENCLATURE = "/api/nomenclature";
	public static final String API_NOMENCLATURES = "/api/nomenclatures";
	public static final String API_NOMENCLATURE_ID = "/api/nomenclature/{id}";
	public static final String API_QUESTIONNAIRE_ID = "/api/questionnaire/{id}";
	public static final String API_QUESTIONNAIRE_ID_METADATA = "/api/questionnaire/{id}/metadata";
	public static final String API_QUESTIONNAIRE_ID_REQUIREDNOMENCLATURE = "/api/questionnaire/{id}/required-nomenclatures";
	public static final String API_QUESTIONNAIREMODELS = "/api/questionnaire-models";
	public static final String API_PARADATAEVENT = "/api/paradata";

	public static final String API_CREATE_DATASET = "/api/create-dataset";

	// Pilotage filter url
	public static final String API_HABILITATION = "/api/check-habilitation";
	public static final String API_PEARLJAM_SURVEYUNITS = "/api/survey-units";
	public static final String API_PEARLJAM_INTERVIEWER_CAMPAIGNS = "/api/interviewer/campaigns";

	// HealthCheck url
	public static final String API_HEALTH_CHECK = "/api/healthcheck";

	// Actuator url
	public static final String API_ACTUATOR = "/actuator/**";

}
