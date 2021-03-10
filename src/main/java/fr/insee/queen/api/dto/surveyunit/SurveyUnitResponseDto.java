package fr.insee.queen.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitResponseDto {
	String id;
	String questionnaireId;
	String campaignId;
	
	public SurveyUnitResponseDto(String id, String questionnaireId, String campaignId) {
		super();
		this.id = id;
		this.questionnaireId = questionnaireId;
		this.campaignId = campaignId;
	}
	
	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getQuestionnaireId() {
		return questionnaireId;
	}
	public void setQuestionnaireId(String questionnaireId) {
		this.questionnaireId = questionnaireId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	


}
