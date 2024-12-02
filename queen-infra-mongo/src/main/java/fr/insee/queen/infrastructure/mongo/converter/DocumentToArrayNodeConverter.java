package fr.insee.queen.infrastructure.mongo.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.List;

@ReadingConverter
@RequiredArgsConstructor
@Slf4j
public class DocumentToArrayNodeConverter implements Converter<List<Document>, ArrayNode> {
    private final ObjectMapper mapper;

    @Override
    public ArrayNode convert(@NonNull List<Document> source) {
            return mapper.valueToTree(source);
    }
}