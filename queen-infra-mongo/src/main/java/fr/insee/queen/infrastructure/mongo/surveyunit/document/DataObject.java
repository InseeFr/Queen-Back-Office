package fr.insee.queen.infrastructure.mongo.surveyunit.document;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
public class DataObject {

    /**
     * The value of data (jsonb format)
     */
    @Field("data")
    private ObjectNode value;

    public static ObjectNode toModel(DataObject data) {
        return data.getValue();
    }

    public static DataObject fromModel(ObjectNode data) {
        return new DataObject(data);
    }
}