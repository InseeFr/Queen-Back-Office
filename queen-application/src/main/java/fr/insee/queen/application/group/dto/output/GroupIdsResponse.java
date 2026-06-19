package fr.insee.queen.application.group.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class GroupIdsResponse {
    @JsonProperty
    private String id;

    public static GroupIdsResponse fromModel(String groupId) {
        return new GroupIdsResponse(groupId);
    }
}
