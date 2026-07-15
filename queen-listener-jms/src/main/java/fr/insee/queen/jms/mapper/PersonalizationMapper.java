package fr.insee.queen.jms.mapper;

import fr.insee.modelefiliere.CoverPageDataDto;
import fr.insee.modelefiliere.InterrogationDto;
import fr.insee.queen.jms.model.Personalization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PersonalizationMapper {

    private final JsonMapper jsonMapper;

    public ArrayNode toArrayNode(InterrogationDto interrogation) {
        CoverPageDataDto cover = interrogation.getExtCoverPageData();
        if (cover == null) {
            return jsonMapper.createArrayNode();
        }
        List<Personalization> personalizations = Stream.of(
                        new Personalization("whoAnswers1", cover.getWhoAnswers1()),
                        new Personalization("whoAnswers2", cover.getWhoAnswers2()),
                        new Personalization("whoAnswers3", cover.getWhoAnswers3()))
                .filter(p -> p.value() != null)
                .toList();
        return jsonMapper.valueToTree(personalizations);
    }
}
