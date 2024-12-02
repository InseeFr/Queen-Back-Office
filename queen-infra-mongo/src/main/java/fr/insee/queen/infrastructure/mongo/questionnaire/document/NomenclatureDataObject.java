package fr.insee.queen.infrastructure.mongo.questionnaire.document;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NomenclatureDataObject {
    @Field("data")
    private ArrayNode values;

    public static NomenclatureDataObject fromModel(ArrayNode value) {
        return new NomenclatureDataObject(value);
    }

    public static ArrayNode toModel(NomenclatureDataObject data) {
        return data.getValues();
    }
}
