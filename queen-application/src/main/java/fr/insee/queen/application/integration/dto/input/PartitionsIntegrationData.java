package fr.insee.queen.application.integration.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.group.model.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "PartitionsIntegration")
public record PartitionsIntegrationData(
        @NotEmpty
        List<@IdValid String> ids,
        @NotBlank
        String label,
        ObjectNode metadata) {

    public static List<Group> toModel(PartitionsIntegrationData data, Set<String> questionnaireIds) {
        ObjectNode metadata = Optional.ofNullable(data.metadata())
                .orElseGet(JsonNodeFactory.instance::objectNode);
        return data.ids().stream()
                .map(id -> new Group(
                        id.toUpperCase(),
                        data.label(),
                        questionnaireIds,
                        metadata))
                .toList();
    }
}
