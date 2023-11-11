package fr.insee.queen.api.integration.controller.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.queen.api.integration.service.model.IntegrationResult;
import fr.insee.queen.api.integration.service.model.IntegrationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IntegrationResultUnitDto {
    @JsonProperty
    private String id;
    @JsonProperty
    private IntegrationStatus status;
    @JsonProperty
    private String cause;

    public static IntegrationResultUnitDto fromModel(IntegrationResult result) {
        return new IntegrationResultUnitDto(result.id(), result.status(), result.cause());
    }

    public static IntegrationResultUnitDto integrationResultUnitCreated(String id) {
        return new IntegrationResultUnitDto(id, IntegrationStatus.CREATED, null);
    }

    public static IntegrationResultUnitDto integrationResultUnitUpdated(String id) {
        return new IntegrationResultUnitDto(id, IntegrationStatus.UPDATED, null);
    }

    public static IntegrationResultUnitDto integrationResultUnitError(String id, String cause) {
        return new IntegrationResultUnitDto(id, IntegrationStatus.ERROR, cause);
    }
}
