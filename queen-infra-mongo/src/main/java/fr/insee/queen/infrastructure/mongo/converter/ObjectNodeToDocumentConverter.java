package fr.insee.queen.infrastructure.mongo.converter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
@RequiredArgsConstructor
public class ObjectNodeToDocumentConverter implements Converter<ObjectNode, Document> {
    @Override
    public Document convert(@NonNull ObjectNode source) {
        return Document.parse(source.toString());
    }
}