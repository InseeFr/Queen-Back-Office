package fr.insee.queen.api.controller.integration.component.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.controller.integration.component.IntegrationResultLabel;
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
import java.io.InputStream;
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
                    String.format(IntegrationResultLabel.FILE_NOT_FOUND, QUESTIONNAIRE_MODELS_XML))));
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
                ObjectNode qmValue = readQuestionnaireStream(qm, zf, questionnaireModelJsonFiles);
                QuestionnaireModelIntegrationInputDto questionnaire = buildQuestionnaireModel(campaignId, qm, qmValue);
                questionnaires.add(questionnaire);
            } catch (IntegrationValidationException ex) {
                resultErrors.add(ex.resultError());
            }
        }

        if(!resultErrors.isEmpty()) {
            throw new IntegrationValidationsException(resultErrors);
        }
        return questionnaires;
    }

    private QuestionnaireModelIntegrationInputDto buildQuestionnaireModel(String campaignId, Element qm, ObjectNode qmValue) throws IntegrationValidationException {
        String qmId = qm.getElementsByTagName(ID).item(0).getTextContent();
        String qmCampaignId = qm.getElementsByTagName(CAMPAIGN_ID).item(0).getTextContent().toUpperCase();

        if(!qmCampaignId.equals(campaignId)) {
            log.info("Questionnaire model has campaign id {} while campaign in zip has id {}", qmCampaignId, campaignId);
            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    qmId,
                    String.format(IntegrationResultLabel.CAMPAIGN_IDS_MISMATCH, qmCampaignId, campaignId))
            );
        }

        String qmLabel = qm.getElementsByTagName(LABEL).item(0).getTextContent();

        NodeList qmNomenclatures = qm.getElementsByTagName(NOMENCLATURE);
        List<String> requiredNomenclatureIds = IntStream.range(0, qmNomenclatures.getLength())
                .filter(j-> qmNomenclatures.item(j).getNodeType() == Node.ELEMENT_NODE)
                .mapToObj(j -> qmNomenclatures.item(j).getTextContent())
                .toList();

        return buildQuestionnaireModel(qmCampaignId, qmId, qmLabel, requiredNomenclatureIds, qmValue);
    }

    private QuestionnaireModelIntegrationInputDto buildQuestionnaireModel(String qmCampaignId, String qmId, String qmLabel, List<String> requiredNomenclatureIds, ObjectNode qmValue) throws IntegrationValidationException {

        QuestionnaireModelIntegrationInputDto questionnaire = new QuestionnaireModelIntegrationInputDto(qmId, qmCampaignId, qmLabel, qmValue, new HashSet<>(requiredNomenclatureIds));
        Set<ConstraintViolation<QuestionnaireModelIntegrationInputDto>> violations = validator.validate(questionnaire);
        if (violations.isEmpty()) {
            return questionnaire;
        }

        StringBuilder violationMessage = new StringBuilder();
        for (ConstraintViolation<QuestionnaireModelIntegrationInputDto> violation : violations) {
            violationMessage.append(violation.getPropertyPath().toString())
                    .append(": ")
                    .append(violation.getMessage())
                    .append(". ");
        }
        throw new IntegrationValidationException(
                new IntegrationResultErrorUnitDto(questionnaire.idQuestionnaireModel(), violationMessage.toString())
        );
    }

    private ObjectNode readQuestionnaireStream(Element qm, ZipFile zipFile, HashMap<String, ZipEntry> questionnaireModelJsonFiles) throws IntegrationValidationException {
        String qmId = qm.getElementsByTagName(ID).item(0).getTextContent();
        String qmFileName = qm.getElementsByTagName(FILENAME).item(0).getTextContent();
        try {
            InputStream questionnaireInputStream = getQuestionnaireInputStream(zipFile, qmId, qmFileName, questionnaireModelJsonFiles);
            return objectMapper.readValue(questionnaireInputStream, ObjectNode.class);
        } catch (IOException e) {
            log.info("Could not parse json in file {}", qmFileName);
            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    qmId,
                    String.format(IntegrationResultLabel.JSON_PARSING_ERROR, qmFileName))
            );
        }
    }

    private InputStream getQuestionnaireInputStream(ZipFile zf, String qmId, String qmFileName, HashMap<String, ZipEntry> questionnaireModelJsonFiles) throws IntegrationValidationException, IOException {
        ZipEntry qmValueEntry = questionnaireModelJsonFiles.get("questionnaireModels/" + qmFileName);
        if(qmValueEntry == null) {
            log.info("Questionnaire model file {} could not be found in input zip", qmFileName);
            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    qmId,
                    String.format(IntegrationResultLabel.QUESTIONNAIRE_FILE_NOT_FOUND, qmFileName))
            );
        }
        return zf.getInputStream(qmValueEntry);
    }
}
