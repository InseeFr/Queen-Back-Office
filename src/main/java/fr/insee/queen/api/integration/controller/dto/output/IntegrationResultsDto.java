package fr.insee.queen.api.integration.controller.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationResultsDto {
    @JsonProperty
    private IntegrationResultUnitDto campaign;
    @JsonProperty
    private List<IntegrationResultUnitDto> nomenclatures;
    @JsonProperty
    private List<IntegrationResultUnitDto> questionnaireModels;
}
