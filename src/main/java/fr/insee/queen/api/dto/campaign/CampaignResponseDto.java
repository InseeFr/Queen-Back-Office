package fr.insee.queen.api.dto.campaign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignResponseDto {
	private String id;
	private List<String> questionnaireIds;
	
	public CampaignResponseDto(String id, List<String> questionnaireIds){
		super();
		this.id=id;
		this.questionnaireIds=questionnaireIds;
	}

	public CampaignResponseDto() {
		super();
	}
	
	public String getId(){
		return this.id;
	}
	

	public void setId(String id){
		this.id = id;
	}
	
	public void setQuestionnaireIds(List<String> questionnaireIds){
		this.questionnaireIds = questionnaireIds;
	}
	
	public List<String> getQuestionnaireIds(){
		return this.questionnaireIds;
	}
	
}
