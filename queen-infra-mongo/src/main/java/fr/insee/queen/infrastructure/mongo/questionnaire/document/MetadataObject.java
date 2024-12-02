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
public class MetadataObject {
    @Field("metadata")
    private ObjectNode value;

    public static MetadataObject fromModel(ObjectNode metadata) {
        return new MetadataObject(metadata);
    }

    public static ObjectNode toModel(MetadataObject metadata) {
        return metadata.getValue();
    }
}
