package fr.insee.queen.api.controller.integration.component;

import fr.insee.queen.api.controller.integration.component.builder.QuestionnaireBuilder;
import fr.insee.queen.api.controller.integration.component.creator.QuestionnaireCreator;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationsException;
import fr.insee.queen.api.dto.input.QuestionnaireModelIntegrationInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
@AllArgsConstructor
@Slf4j
public class QuestionnaireIntegrationComponent {
    private final QuestionnaireBuilder questionnaireBuilder;
    private final QuestionnaireCreator questionnaireCreator;
    public List<IntegrationResultUnitDto> process(String campaignId, ZipEntry questionnaireModelsXmlFile, HashMap<String, ZipEntry> questionnaireModelJsonFiles, ZipFile zf) throws ParserConfigurationException, IOException, SAXException {
        try {
            List<QuestionnaireModelIntegrationInputDto> questionnaires = questionnaireBuilder.build(campaignId, zf, questionnaireModelsXmlFile, questionnaireModelJsonFiles);;
            return questionnaireCreator.create(questionnaires);
        } catch (IntegrationValidationsException e) {
            return e.resultErrors().stream()
                    .map(IntegrationResultUnitDto.class::cast)
                    .toList();
        }
    }
}
