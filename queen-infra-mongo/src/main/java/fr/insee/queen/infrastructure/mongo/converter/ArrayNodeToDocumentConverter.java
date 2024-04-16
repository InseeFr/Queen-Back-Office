package fr.insee.queen.infrastructure.mongo.converter;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.BsonArray;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
@RequiredArgsConstructor
public class ArrayNodeToDocumentConverter implements Converter<ArrayNode, BsonArray> {
    @Override
    public BsonArray convert(@NonNull ArrayNode source) {
        return BsonArray.parse(source.toString());
    }
}