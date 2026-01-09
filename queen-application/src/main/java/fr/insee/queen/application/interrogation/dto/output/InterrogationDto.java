package fr.insee.queen.application.interrogation.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Interrogation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InterrogationDto(
        String id,
        String questionnaireId,
        @Schema(ref = SchemaType.Names.PERSONALIZATION)
        ArrayNode personalization,
        @Schema(ref = SchemaType.Names.DATA)
        ObjectNode data,
        StateDataDto stateData) {

    public static InterrogationDto createInterrogationNOKDto(String id) {
        return new InterrogationDto(id, null, null, null, null);
    }

    public static InterrogationDto createInterrogationOKDtoWithStateData(String id, StateData stateData) {
        return new InterrogationDto(id, null, null, null, StateDataDto.fromModel(stateData));
    }

    public static InterrogationDto createInterrogationOKDtoWithQuestionnaireModel(String id, String questionnaireModelId) {
        return new InterrogationDto(id, questionnaireModelId, null, null, null);
    }

    public static InterrogationDto fromModel(Interrogation interrogation) {
        return new InterrogationDto(interrogation.id(), interrogation.questionnaireId(),
                interrogation.personalization(),
                interrogation.data(),
                StateDataDto.fromModel(interrogation.stateData()));
    }

    public static InterrogationDto fromSensitiveModel(Interrogation interrogation) {
        return new InterrogationDto(interrogation.id(), interrogation.questionnaireId(),
                interrogation.personalization(),
                JsonNodeFactory.instance.objectNode(),
                StateDataDto.fromModel(interrogation.stateData()));
    }
}
