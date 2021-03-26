package fr.insee.queen.api.constants;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}
	
	// User
	public static final String GUEST = "GUEST";
	public static final String CAMPAIGN = "campaign";
	public static final String AUTHORIZATION = "Authorization";
	
	//API url for endpoints
	public static final String API_CAMPAIGNS = "/api/campaigns";
	public static final String API_CAMPAIGN_SURVEY_UNITS = "/api/campaign/{idCampaign}/survey-units";
	public static final String API_CAMPAIGN_SURVEY_UNITS_CREATE = "/api/campaign/{idCampaign}/survey-unit";
	public static final String API_CAMPAIGN_QUESTIONAIRE = "/api/campaign/{idCampaign}/questionnaire";
	public static final String API_CAMPAIGN_QUESTIONAIRE_ID = "/api/campaign/{idCampaign}/questionnaire-id";
	public static final String API_CAMPAIGN_REQUIRED_NOMENCLATURES = "/api/campaign/{id}/required-nomenclatures";
	public static final String API_SURVEY_UNIT= "/api/survey-unit/{id}";
	public static final String API_SURVEY_UNIT_DATA = "/api/survey-unit/{id}/data";
	public static final String API_SURVEY_UNIT_COMMENT = "/api/survey-unit/{id}/comment";
	public static final String API_SURVEY_UNIT_STATE_DATA = "/api/survey-unit/{id}/state-data";
	public static final String API_SURVEY_UNIT_DEPOSIT_PROOF = "/api/survey-unit/{id}/deposit-proof";
	public static final String API_SURVEY_UNIT_PERSONALIZATION = "/api/survey-unit/{id}/personalization";
	public static final String API_NOMENCLATURE = "/api/nomenclature/{id}";
	public static final String API_NOMENCLATURE_POST = "/api/nomenclature";
	public static final String API_QUESTIONNAIRE = "/api/questionnaire/{id}";
	public static final String API_QUESTIONNAIRE_CREATE_QUESTIONNAIRE = "/api/questionnaire-models";
	public static final String API_PARADATAEVENT = "/api/paradata";
	public static final String API_QUESTIONNAIRE_NOMENCLATURE = "/api/questionnaire/{id}/required-nomenclatures";
	
	public static final String API_CREATE_DATASET = "/api/createDataSet";

	//Pilotage filter url
	public static final String API_HABILITATION = "/api/check-habilitation";
	public static final String API_PEARLJAM_SURVEY_UNITS = "/api/survey-units";
	
	

}
