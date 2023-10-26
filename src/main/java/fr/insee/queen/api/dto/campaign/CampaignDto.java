package fr.insee.queen.api.dto.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude
public record CampaignDto(String id, String label) {

}
