package fr.insee.queen.api.controller.integration.component.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.controller.integration.component.SchemaComponent;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationsException;
import fr.insee.queen.api.dto.input.QuestionnaireModelIntegrationInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
@Slf4j
@AllArgsConstructor
public class QuestionnaireBuilder {
    private final SchemaComponent schemaComponent;
    private final Validator validator;
    private static final String LABEL = "Label";
    private static final String ID = "Id";
    private static final String FILENAME = "FileName";
    private static final String CAMPAIGN_ID = "CampaignId";
    private static final String NOMENCLATURE = "Nomenclature";
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final String QUESTIONNAIRE_MODELS_XML = "questionnaireModels.xml";

    public List<QuestionnaireModelIntegrationInputDto> build(String campaignId, ZipFile zf, ZipEntry questionnaireModelsXmlFile,
                                                             HashMap<String, ZipEntry> questionnaireModelsJsonFiles) throws ParserConfigurationException, IOException, SAXException, IntegrationValidationsException {
        if(questionnaireModelsXmlFile == null) {
            throw new IntegrationValidationsException(List.of(new IntegrationResultErrorUnitDto(
                    QUESTIONNAIRE_MODELS_XML,
                    String.format("No file %s found", QUESTIONNAIRE_MODELS_XML))));
        }

        try {
            schemaComponent.throwExceptionIfXmlDataFileNotValid(zf, questionnaireModelsXmlFile, "questionnaireModels_integration_template.xsd");
        } catch (IntegrationValidationException ex) {
            throw new IntegrationValidationsException(List.of(ex.resultError()));
        }
        return buildQuestionnaireModels(campaignId, zf, questionnaireModelsXmlFile, questionnaireModelsJsonFiles);
    }

    private List<QuestionnaireModelIntegrationInputDto> buildQuestionnaireModels(String campaignId, ZipFile zf, ZipEntry questionnaireModelsXmlFile,
                                                                      HashMap<String, ZipEntry> questionnaireModelJsonFiles) throws IntegrationValidationsException, ParserConfigurationException, SAXException, IOException {
        List<QuestionnaireModelIntegrationInputDto> questionnaires = new ArrayList<>();
        List<IntegrationResultErrorUnitDto> resultErrors = new ArrayList<>();

        Document doc = schemaComponent.buildDocument(zf.getInputStream(questionnaireModelsXmlFile));
        NodeList qmNodes = doc.getElementsByTagName("QuestionnaireModels").item(0).getChildNodes();
        for (int i = 0; i < qmNodes.getLength(); i++) {
            if(qmNodes.item(i).getNodeType() != Node.ELEMENT_NODE){
                continue;
            }
            Element qm = (Element) qmNodes.item(i);
            try {
                questionnaires.add(buildQuestionnaireModel(campaignId, zf, qm, questionnaireModelJsonFiles));
            } catch (IntegrationValidationException ex) {
                resultErrors.add(ex.resultError());
            }
        }

        if(!resultErrors.isEmpty()) {
            throw new IntegrationValidationsException(resultErrors);
        }
        return questionnaires;
    }

    private QuestionnaireModelIntegrationInputDto buildQuestionnaireModel(String campaignId, ZipFile zf, Element qm,
                                           HashMap<String, ZipEntry> questionnaireModelJsonFiles) throws IntegrationValidationException {
        String qmId = qm.getElementsByTagName(ID).item(0).getTextContent();
        String qmLabel = qm.getElementsByTagName(LABEL).item(0).getTextContent();
        String qmCampaignId = qm.getElementsByTagName(CAMPAIGN_ID).item(0).getTextContent();
        String qmFilename = qm.getElementsByTagName(FILENAME).item(0).getTextContent();
        NodeList requiredNomNodes = qm.getElementsByTagName(NOMENCLATURE);
        List<String> requiredNomenclatureIds = IntStream.range(0, requiredNomNodes.getLength())
                .filter(j-> requiredNomNodes.item(j).getNodeType() == Node.ELEMENT_NODE)
                .mapToObj(j -> requiredNomNodes.item(j).getTextContent())
                .toList();

        ZipEntry qmValueEntry = questionnaireModelJsonFiles.get("questionnaireModels/" +qmFilename);
        if(qmValueEntry == null) {
            log.info("Questionnaire model file {} could not be found in input zip", qmFilename);
            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    qmId,
                    "Questionnaire model file '" + qmFilename + "' could not be found in input zip")
            );
        }

        if(!qmCampaignId.equals(campaignId)) {
            log.info("Questionnaire model has campaign id {} while campaign in zip has id {}", qmCampaignId, campaignId);
            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    qmId,
                    String.format("Questionnaire model has campaign id %s while campaign in zip has id %s", qmCampaignId, campaignId))
            );
        }

        ObjectNode qmValue;
        try {
            qmValue = objectMapper.readValue(zf.getInputStream(qmValueEntry), ObjectNode.class);
            QuestionnaireModelIntegrationInputDto questionnaire = new QuestionnaireModelIntegrationInputDto(qmId, campaignId, qmLabel, qmValue, new HashSet<>(requiredNomenclatureIds));
            Set<ConstraintViolation<QuestionnaireModelIntegrationInputDto>> violations = validator.validate(questionnaire);
            if (violations.isEmpty()) {
                return questionnaire;
            }

            String violationMessage = "";
            for(ConstraintViolation<QuestionnaireModelIntegrationInputDto> violation : violations) {
                violationMessage += violation.getPropertyPath().toString() + ": " + violation.getMessage() + ". ";
            }
            throw new IntegrationValidationException(
                    new IntegrationResultErrorUnitDto(questionnaire.idQuestionnaireModel(), violationMessage)
            );
        } catch (IOException e) {
            log.info("Could not parse json in file {}", qmFilename);
            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    qmId,
                    "Could not parse json in file '" + qmFilename + "'")
            );
        }
    }
}
