package fr.insee.queen.infrastructure.mongo.surveyunit.document;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
public class PersonalizationObject {

    @Field("personalization")
    private ArrayNode value;

    public static ArrayNode toModel(PersonalizationObject personalization) {
        return personalization.getValue();
    }

    public static PersonalizationObject fromModel(ArrayNode personalization) {
        return new PersonalizationObject(personalization);
    }
}