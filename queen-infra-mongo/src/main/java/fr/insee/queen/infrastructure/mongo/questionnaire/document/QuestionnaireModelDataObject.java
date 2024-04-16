package fr.insee.queen.infrastructure.mongo.questionnaire.document;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireModelDataObject {
    @Field("data")
    private ObjectNode value;

    public static QuestionnaireModelDataObject fromModel(ObjectNode value) {
        return new QuestionnaireModelDataObject(value);
    }

    public static ObjectNode toModel(QuestionnaireModelDataObject data) {
        return data.getValue();
    }
}
