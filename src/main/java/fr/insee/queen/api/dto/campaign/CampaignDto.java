package fr.insee.queen.api.dto.campaign;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.queen.api.dto.metadata.MetadataDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDto {
	public String id;
	
	public String label;
	
	public Set<String> questionnaireIds;
	
	public MetadataDto metadata;

	public CampaignDto() {
		super();
	}
	public CampaignDto(String id, String label, Set<String> questionnaireIds, MetadataDto metadata) {
		super();
		this.id = id;
		this.label = label;
		this.questionnaireIds = questionnaireIds;
		this.metadata = metadata;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the questionnaireIds
	 */
	public Set<String> getQuestionnaireIds() {
		return questionnaireIds;
	}
	/**
	 * @return the metadata
	 */
	public MetadataDto getMetadata() {
		return metadata;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @param questionnaireIds the questionnaireIds to set
	 */
	public void setQuestionnaireIds(Set<String> questionnaireIds) {
		this.questionnaireIds = questionnaireIds;
	}
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(MetadataDto metadata) {
		this.metadata = metadata;
	}
}
