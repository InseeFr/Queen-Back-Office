package fr.insee.queen.api.dto.campaign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class CampaignSummaryDto {
    @JsonProperty
    private String id;
    @JsonProperty
    private List<String> questionnaireIds;
}
