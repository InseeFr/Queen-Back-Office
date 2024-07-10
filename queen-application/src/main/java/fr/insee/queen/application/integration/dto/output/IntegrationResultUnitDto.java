package fr.insee.queen.application.integration.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Schema(name = "IntegrationResultUnit")
public class IntegrationResultUnitDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1905122041950251207L;

    @JsonProperty
    private final String id;
    @JsonProperty
    private final IntegrationStatus status;
    @JsonProperty
    private final String cause;

    public static IntegrationResultUnitDto fromModel(IntegrationResult result) {
        return new IntegrationResultUnitDto(result.getId(), result.getStatus(), result.getCause());
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
