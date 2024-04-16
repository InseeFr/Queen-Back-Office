package fr.insee.queen.infrastructure.mongo.surveyunit.document;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
public class CommentObject {
    /**
     * The value of data (jsonb format)
     */
    @Field("comment")
    private ObjectNode value;

    public static ObjectNode toModel(CommentObject comment) {
        return comment.getValue();
    }

    public static CommentObject fromModel(ObjectNode comment) {
        return new CommentObject(comment);
    }
}