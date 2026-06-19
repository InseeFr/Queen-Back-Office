package fr.insee.queen.application.group.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.model.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@Schema(name = "Group")
public class GroupResponse {
    @JsonProperty
    private String id;
    @JsonProperty
    private List<String> questionnaireIds;
    @JsonProperty
    private ObjectNode metadata;


    public static GroupResponse fromModel(Group group) {
        return new GroupResponse(group.getId(),
                group.getQuestionnaireIds().stream().toList(),
                group.getMetadata());
    }
}
