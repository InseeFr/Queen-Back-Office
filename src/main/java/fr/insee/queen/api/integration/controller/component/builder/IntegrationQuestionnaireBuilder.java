package fr.insee.queen.api.integration.controller.component.builder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.integration.controller.component.builder.schema.SchemaComponent;
import fr.insee.queen.api.integration.controller.component.exception.IntegrationValidationException;
import fr.insee.queen.api.integration.controller.dto.input.QuestionnaireModelIntegrationData;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.api.integration.service.IntegrationService;
import fr.insee.queen.api.integration.service.model.IntegrationResult;
import fr.insee.queen.api.integration.service.model.IntegrationResultLabel;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handle the integration of questionnaires
 */
@Component
@Slf4j
@AllArgsConstructor
public class IntegrationQuestionnaireBuilder implements QuestionnaireBuilder {
    private final SchemaComponent schemaComponent;
    private final Validator validator;
    private final IntegrationService integrationService;
    private final ObjectMapper mapper;
    private static final String LABEL = "Label";
    private static final String ID = "Id";
    private static final String FILENAME = "FileName";
    private static final String CAMPAIGN_ID = "CampaignId";
    private static final String NOMENCLATURE = "Nomenclature";
    public static final String QUESTIONNAIRE_MODELS_XML = "questionnaireModels.xml";
    public static final String QUESTIONNAIRE_MODELS_JSON = "questionnaireModels.json";

    @Override
    public List<IntegrationResultUnitDto> build(String campaignId, ZipFile integrationZipFile, boolean isXmlIntegration) {
        try {
            schemaComponent.throwExceptionIfXmlDataFileNotValid(integrationZipFile, QUESTIONNAIRE_MODELS_XML, "questionnaireModels_integration_template.xsd");
        } catch (IntegrationValidationException ex) {
            return List.of(ex.getResultError());
        }

        if(isXmlIntegration) {
            return buildXmlQuestionnaireModels(campaignId, integrationZipFile);
        }
        return buildQuestionnaireModels(campaignId, integrationZipFile);
    }

    private List<IntegrationResultUnitDto> buildXmlQuestionnaireModels(String campaignId, ZipFile zf) {
        List<IntegrationResultUnitDto> results = new ArrayList<>();
        Document doc;
        try {
            doc = schemaComponent.buildDocument(zf.getInputStream(zf.getEntry(QUESTIONNAIRE_MODELS_XML)));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            results.add(IntegrationResultUnitDto.integrationResultUnitError(null, e.getMessage()));
            return results;
        }

        NodeList qmNodes = doc.getElementsByTagName("QuestionnaireModels").item(0).getChildNodes();
        for (int i = 0; i < qmNodes.getLength(); i++) {
            if (qmNodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element qm = (Element) qmNodes.item(i);
            results.addAll(buildXmlQuestionnaireModel(campaignId, qm, zf));
        }
        return results;
    }

    private List<IntegrationResultUnitDto> buildQuestionnaireModels(String campaignId, ZipFile zf) {

        List<IntegrationResultUnitDto> results = new ArrayList<>();
        try {
            schemaComponent.throwExceptionIfDataFileNotExist(zf, QUESTIONNAIRE_MODELS_JSON);
            ZipEntry zipQuestionnairesFile = zf.getEntry(QUESTIONNAIRE_MODELS_JSON);
            List<QuestionnaireModelItem> questionnaireModelItems = mapper.readValue(zf.getInputStream(zipQuestionnairesFile), new TypeReference<List<QuestionnaireModelItem>>(){});
            for(QuestionnaireModelItem questionnaireModelItem : questionnaireModelItems) {
                ObjectNode qmValue = readQuestionnaireStream(questionnaireModelItem, zf);
                results.addAll(buildQuestionnaireModel(campaignId, questionnaireModelItem, qmValue));
            }
        } catch (IntegrationValidationException ex) {
            results.add(ex.getResultError());
        }  catch (IOException e) {
            IntegrationResultUnitDto resultError = IntegrationResultUnitDto.integrationResultUnitError(
                    null,
                    String.format(IntegrationResultLabel.JSON_PARSING_ERROR, QUESTIONNAIRE_MODELS_JSON));
            results.add(resultError);
        }
        return results;
    }

    private List<IntegrationResultUnitDto> buildXmlQuestionnaireModel(String campaignId, Element qm, ZipFile zf) {
        String qmId = qm.getElementsByTagName(ID).item(0).getTextContent();
        String qmCampaignId = qm.getElementsByTagName(CAMPAIGN_ID).item(0).getTextContent().toUpperCase();
        String qmFileName = qm.getElementsByTagName(FILENAME).item(0).getTextContent();

        if (!qmCampaignId.equals(campaignId)) {
            log.info("Questionnaire model has campaign id {} while campaign in zip has id {}", qmCampaignId, campaignId);
            List<IntegrationResultUnitDto> results = new ArrayList<>();
            results.add(IntegrationResultUnitDto.integrationResultUnitError(
                    qmId,
                    String.format(IntegrationResultLabel.CAMPAIGN_IDS_MISMATCH, qmCampaignId, campaignId))
            );
            return results;
        }

        String qmLabel = qm.getElementsByTagName(LABEL).item(0).getTextContent();


        NodeList qmNomenclatures = qm.getElementsByTagName(NOMENCLATURE);
        List<String> requiredNomenclatureIds = IntStream.range(0, qmNomenclatures.getLength())
                .filter(j -> qmNomenclatures.item(j).getNodeType() == Node.ELEMENT_NODE)
                .mapToObj(j -> qmNomenclatures.item(j).getTextContent())
                .toList();

        QuestionnaireModelItem questionnaireModelItem = new QuestionnaireModelItem(qmId, qmLabel, qmFileName, requiredNomenclatureIds);
        try {
            ObjectNode qmValue = readQuestionnaireStream(questionnaireModelItem, zf);
            return buildQuestionnaireModel(qmCampaignId, questionnaireModelItem, qmValue);
        } catch (IntegrationValidationException ex) {
            List<IntegrationResultUnitDto> results = new ArrayList<>();
            results.add(ex.getResultError());
            return results;
        }


    }

    private List<IntegrationResultUnitDto> buildQuestionnaireModel(String qmCampaignId, QuestionnaireModelItem questionnaireModelItem, ObjectNode qmValue) {
        QuestionnaireModelIntegrationData questionnaire = new QuestionnaireModelIntegrationData(questionnaireModelItem.id(),
                qmCampaignId,
                questionnaireModelItem.label(),
                qmValue,
                new HashSet<>(questionnaireModelItem.requiredNomenclatures()));
        Set<ConstraintViolation<QuestionnaireModelIntegrationData>> violations = validator.validate(questionnaire);
        if (!violations.isEmpty()) {
            StringBuilder violationMessage = new StringBuilder();
            for (ConstraintViolation<QuestionnaireModelIntegrationData> violation : violations) {
                violationMessage.append(violation.getPropertyPath().toString())
                        .append(": ")
                        .append(violation.getMessage())
                        .append(". ");
            }
            List<IntegrationResultUnitDto> results = new ArrayList<>();
            results.add(
                    IntegrationResultUnitDto.integrationResultUnitError(questionnaire.idQuestionnaireModel(), violationMessage.toString())
            );
            return results;
        }
        List<IntegrationResult> results = integrationService.create(QuestionnaireModelIntegrationData.toModel(questionnaire));
        return results.stream().map(IntegrationResultUnitDto::fromModel).toList();
    }

    private ObjectNode readQuestionnaireStream(QuestionnaireModelItem questionnaireModelItem, ZipFile zipFile) throws IntegrationValidationException {
        try {
            InputStream questionnaireInputStream = getQuestionnaireInputStream(zipFile, questionnaireModelItem);
            return mapper.readValue(questionnaireInputStream, ObjectNode.class);
        } catch (IOException e) {
            log.info("Could not parse json in file {}", questionnaireModelItem.fileName());
            throw new IntegrationValidationException(IntegrationResultUnitDto.integrationResultUnitError(
                    questionnaireModelItem.id(),
                    String.format(IntegrationResultLabel.JSON_PARSING_ERROR, questionnaireModelItem.fileName()))
            );
        }
    }

    private InputStream getQuestionnaireInputStream(ZipFile zf, QuestionnaireModelItem questionnaireModelItem) throws IntegrationValidationException, IOException {
        String qmFileName = questionnaireModelItem.fileName();
        ZipEntry qmValueEntry = zf.getEntry("questionnaireModels/" + qmFileName);
        if (qmValueEntry == null) {
            log.info("Questionnaire model file {} could not be found in input zip", qmFileName);
            throw new IntegrationValidationException(IntegrationResultUnitDto.integrationResultUnitError(
                    questionnaireModelItem.id(),
                    String.format(IntegrationResultLabel.QUESTIONNAIRE_FILE_NOT_FOUND, qmFileName))
            );
        }
        return zf.getInputStream(qmValueEntry);
    }
}
