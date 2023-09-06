package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignInputDto(
	String id,
	String label,
	Set<String> questionnaireIds,
	MetadataInputDto metadata){
}
