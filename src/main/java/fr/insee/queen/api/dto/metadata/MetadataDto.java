package fr.insee.queen.api.dto.metadata;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.Metadata;

public class MetadataDto {
	public JsonNode value;
	
	public MetadataDto() {
		super();
	}

	public MetadataDto(JsonNode value) {
		super();
		this.value = value;
	}
	
	public MetadataDto(Metadata metadata) {
		super();
		this.value = metadata.getValue();
	}

	/**
	 * @return the value
	 */
	public JsonNode getValue() {
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(JsonNode value) {
		this.value = value;
	}
}
