package fr.insee.queen.domain.group.model;

import tools.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireModel {
    private String id;
    private String groupId;
    private String label;
    private ObjectNode value;
    private Set<String> requiredNomenclatureIds;

    public static QuestionnaireModel createQuestionnaireWithGroup(String id, String label, ObjectNode value, Set<String> requiredNomenclatureIds, @NonNull String groupId) {
        return new QuestionnaireModel(id, groupId, label, value, requiredNomenclatureIds);
    }

    public static QuestionnaireModel createQuestionnaireWithoutGroup(String id, String label, ObjectNode value, Set<String> requiredNomenclatureIds) {
        return new QuestionnaireModel(id, null, label, value, requiredNomenclatureIds);
    }
}
