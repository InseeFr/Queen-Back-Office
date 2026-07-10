package fr.insee.queen.application.group.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.queen.domain.group.model.GroupSummary;
import fr.insee.queen.domain.pilotage.model.PilotageGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@Schema(name = "GroupSummary")
public class GroupSummaryResponse {
    @JsonProperty
    private String id;
    @JsonProperty
    private List<String> questionnaireIds;

    public static GroupSummaryResponse fromModel(GroupSummary group) {
        return new GroupSummaryResponse(group.getId(), group.getQuestionnaireIds().stream().toList());
    }

    public static GroupSummaryResponse fromPilotageModel(PilotageGroup group) {
        return new GroupSummaryResponse(group.id(), group.questionnaireIds().stream().toList());
    }
}
