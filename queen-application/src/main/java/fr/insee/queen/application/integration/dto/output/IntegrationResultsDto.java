package fr.insee.queen.application.integration.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "IntegrationResults")
public class IntegrationResultsDto {
    @JsonProperty
    private IntegrationResultUnitDto campaign;
    @JsonProperty
    private List<IntegrationResultUnitDto> nomenclatures;
    @JsonProperty
    private List<IntegrationResultUnitDto> questionnaireModels;
}
