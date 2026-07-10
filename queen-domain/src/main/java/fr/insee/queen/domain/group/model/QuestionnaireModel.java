package fr.insee.queen.domain.group.model;

import tools.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireModel {
    private String id;
    private String label;
    private ObjectNode value;
    private Set<String> requiredNomenclatureIds;

    public static QuestionnaireModel create(String id, String label, ObjectNode value, Set<String> requiredNomenclatureIds) {
        return new QuestionnaireModel(id, label, value, requiredNomenclatureIds);
    }
}
