package fr.insee.queen.api.dto.questionnairemodel;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public class QuestionnaireModelCreateDto {
	
	private String idQuestionnaireModel;
	
	private String label;
	
	private JsonNode value;

	private Set<String> requiredNomenclatureIds;

	/**
	 * @return the idQuestionnaireModel
	 */
	public String getIdQuestionnaireModel() {
		return idQuestionnaireModel;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the value
	 */
	public JsonNode getValue() {
		return value;
	}

	/**
	 * @return the requiredNomenclatureIds
	 */
	public Set<String> getRequiredNomenclatureIds() {
		return requiredNomenclatureIds;
	}

	/**
	 * @param idQuestionnaireModel the idQuestionnaireModel to set
	 */
	public void setIdQuestionnaireModel(String idQuestionnaireModel) {
		this.idQuestionnaireModel = idQuestionnaireModel;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(JsonNode value) {
		this.value = value;
	}

	/**
	 * @param requiredNomenclatureIds the requiredNomenclatureIds to set
	 */
	public void setRequiredNomenclatureIds(Set<String> requiredNomenclatureIds) {
		this.requiredNomenclatureIds = requiredNomenclatureIds;
	}

}
