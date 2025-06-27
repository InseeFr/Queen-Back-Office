package fr.insee.queen.application.interrogationtempzone.dto.output;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.interrogationtempzone.model.InterrogationTempZone;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "InterrogationTempZone")
public record InterrogationTempZoneDto(
        UUID id,
        String interrogationId,
        String userId,
        Long date,
        @Schema(ref = SchemaType.Names.INTERROGATION_TEMP_ZONE)
        ObjectNode interrogation) {

    public static InterrogationTempZoneDto fromModel(InterrogationTempZone interrogationTempZone) {
        return new InterrogationTempZoneDto(interrogationTempZone.id(), interrogationTempZone.interrogationId(), interrogationTempZone.userId(), interrogationTempZone.date(), interrogationTempZone.interrogation());
    }
}
