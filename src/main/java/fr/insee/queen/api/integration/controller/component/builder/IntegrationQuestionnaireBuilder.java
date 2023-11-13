package fr.insee.queen.api.integration.controller.component.builder;

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
    private final ObjectMapper objectMapper;
    private static final String LABEL = "Label";
    private static final String ID = "Id";
    private static final String FILENAME = "FileName";
    private static final String CAMPAIGN_ID = "CampaignId";
    private static final String NOMENCLATURE = "Nomenclature";
    public static final String QUESTIONNAIRE_MODELS_XML = "questionnaireModels.xml";

    @Override
    public List<IntegrationResultUnitDto> build(String campaignId, ZipFile integrationZipFile) {
        try {
            schemaComponent.throwExceptionIfXmlDataFileNotValid(integrationZipFile, QUESTIONNAIRE_MODELS_XML, "questionnaireModels_integration_template.xsd");
        } catch (IntegrationValidationException ex) {
            return List.of(ex.resultError());
        }
        return buildQuestionnaireModels(campaignId, integrationZipFile);
    }

    private List<IntegrationResultUnitDto> buildQuestionnaireModels(String campaignId, ZipFile zf) {
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
            try {
                ObjectNode qmValue = readQuestionnaireStream(qm, zf);
                results.addAll(buildQuestionnaireModel(campaignId, qm, qmValue));
            } catch (IntegrationValidationException ex) {
                results.add(ex.resultError());
            }
        }
        return results;
    }

    private List<IntegrationResultUnitDto> buildQuestionnaireModel(String campaignId, Element qm, ObjectNode qmValue) {
        String qmId = qm.getElementsByTagName(ID).item(0).getTextContent();
        String qmCampaignId = qm.getElementsByTagName(CAMPAIGN_ID).item(0).getTextContent().toUpperCase();

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

        return buildQuestionnaireModel(qmCampaignId, qmId, qmLabel, requiredNomenclatureIds, qmValue);
    }

    private List<IntegrationResultUnitDto> buildQuestionnaireModel(String qmCampaignId, String qmId, String qmLabel, List<String> requiredNomenclatureIds, ObjectNode qmValue) {
        QuestionnaireModelIntegrationData questionnaire = new QuestionnaireModelIntegrationData(qmId, qmCampaignId, qmLabel, qmValue, new HashSet<>(requiredNomenclatureIds));
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

    private ObjectNode readQuestionnaireStream(Element qm, ZipFile zipFile) throws IntegrationValidationException {
        String qmId = qm.getElementsByTagName(ID).item(0).getTextContent();
        String qmFileName = qm.getElementsByTagName(FILENAME).item(0).getTextContent();
        try {
            InputStream questionnaireInputStream = getQuestionnaireInputStream(zipFile, qmId, qmFileName);
            return objectMapper.readValue(questionnaireInputStream, ObjectNode.class);
        } catch (IOException e) {
            log.info("Could not parse json in file {}", qmFileName);
            throw new IntegrationValidationException(IntegrationResultUnitDto.integrationResultUnitError(
                    qmId,
                    String.format(IntegrationResultLabel.JSON_PARSING_ERROR, qmFileName))
            );
        }
    }

    private InputStream getQuestionnaireInputStream(ZipFile zf, String qmId, String qmFileName) throws IntegrationValidationException, IOException {
        ZipEntry qmValueEntry = zf.getEntry("questionnaireModels/" + qmFileName);
        if (qmValueEntry == null) {
            log.info("Questionnaire model file {} could not be found in input zip", qmFileName);
            throw new IntegrationValidationException(IntegrationResultUnitDto.integrationResultUnitError(
                    qmId,
                    String.format(IntegrationResultLabel.QUESTIONNAIRE_FILE_NOT_FOUND, qmFileName))
            );
        }
        return zf.getInputStream(qmValueEntry);
    }
}
