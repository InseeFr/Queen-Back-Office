package fr.insee.queen.infrastructure.mongo.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.mongo.converter.exception.JsonParsingObjectException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
@RequiredArgsConstructor
@Slf4j
public class DocumentToObjectNodeConverter implements Converter<Document, ObjectNode> {
    private final ObjectMapper mapper;

    @Override
    public ObjectNode convert(@NonNull Document source) {
        try {
            return mapper.readValue(source.toJson(), ObjectNode.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingObjectException(e.getMessage());
        }
    }
}