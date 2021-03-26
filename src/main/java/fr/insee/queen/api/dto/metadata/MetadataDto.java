package fr.insee.queen.api.dto.metadata;

import com.fasterxml.jackson.databind.JsonNode;

public class MetadataDto {
	private JsonNode value;
	
	public MetadataDto() {
		super();
	}
	
	public MetadataDto(JsonNode value) {
		this.setValue(value);
	}

	public JsonNode getValue() {
		return value;
	}

	public void setValue(JsonNode value) {
		this.value = value;
	}
}
