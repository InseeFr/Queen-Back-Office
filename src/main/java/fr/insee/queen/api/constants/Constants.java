package fr.insee.queen.api.constants;

import java.io.InputStream;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}
	
	// User
	public static final String GUEST = "GUEST";
	
	//API url for endpoints
	public static final String API_CAMPAIGNS = "/api/campaigns";
	public static final String API_CAMPAIGN_SURVEY_UNITS = "/api/campaign/{idCampaign}/survey-units";
	public static final String API_CAMPAIGN_QUESTIONAIRE = "/api/campaign/{idCampaign}/questionnaire";
	public static final String API_CAMPAIGN_QUESTIONAIRE_ID = "/api/campaign/{idCampaign}/questionnaire-id";
	public static final String API_CAMPAIGN_REQUIRED_NOMENCLATURES = "/api/campaign/{id}/required-nomenclatures";
	public static final String API_SURVEY_UNIT_DATA = "/api/survey-unit/{id}/data";
  public static final String API_SURVEY_UNIT_COMMENT = "/api/survey-unit/{id}/comment";
  public static final String API_SURVEY_UNIT_STATE_DATA = "/api/survey-unit/{id}/comment";
	public static final String API_NOMENCLATURE = "/api/nomenclature/{id}";
	public static final String API_QUESTIONNAIRE = "/api/questionnaire/{id}";
	public static final String API_PARADATAEVENT = "/api/paradata";
	public static final String API_QUESTIONNAIRE_NOMENCLATURE = "/api/questionnaire/{id}/required-nomenclatures";
		

	//PearlJam filter url
	public static final String API_PEARLJAM_SURVEY_UNITS = "/api/survey-units";
	

}
