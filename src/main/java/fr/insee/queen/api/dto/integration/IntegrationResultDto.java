package fr.insee.queen.api.dto.integration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationResultDto {
	IntegrationResultUnitDto campaign;
	List<IntegrationResultUnitDto> nomenclatures;
	List<IntegrationResultUnitDto> questionnaireModels;
	
	public IntegrationResultDto() {
		super();
	}
	public IntegrationResultUnitDto getCampaign() {
		return campaign;
	}
	public void setCampaign(IntegrationResultUnitDto campaign) {
		this.campaign = campaign;
	}
	public List<IntegrationResultUnitDto> getNomenclatures() {
		return nomenclatures;
	}
	public void setNomenclatures(List<IntegrationResultUnitDto> nomenclatures) {
		this.nomenclatures = nomenclatures;
	}
	public List<IntegrationResultUnitDto> getQuestionnaireModels() {
		return questionnaireModels;
	}
	public void setQuestionnaireModels(List<IntegrationResultUnitDto> questionnaireModels) {
		this.questionnaireModels = questionnaireModels;
	}
}
