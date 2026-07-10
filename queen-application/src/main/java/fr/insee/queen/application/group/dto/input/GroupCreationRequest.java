package fr.insee.queen.application.group.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.group.model.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

/**
 * Data used for group creation
 *
 * @param id group id
 * @param label group label
 * @param questionnaireIds list of questionnaire ids linked to the group
 * @param metadata group metadata
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "GroupCreation")
public record GroupCreationRequest(
        @IdValid
        String id,
        @NotBlank
        String label,
        @NotEmpty
        Set<String> questionnaireIds,
        @Valid
        MetadataCreationData metadata) {

    public static Group toModel(GroupCreationRequest group) {
        ObjectNode metadataValue = JsonNodeFactory.instance.objectNode();
        if (group.metadata() != null) {
            metadataValue = group.metadata.value();
        }

        return new Group(group.id, group.label, group.questionnaireIds, metadataValue);
    }
}
