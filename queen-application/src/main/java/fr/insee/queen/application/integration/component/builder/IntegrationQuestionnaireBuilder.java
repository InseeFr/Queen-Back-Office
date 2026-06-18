package fr.insee.queen.application.integration.component.builder;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.integration.component.builder.schema.SchemaComponent;
import fr.insee.queen.application.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.application.integration.dto.input.QuestionnaireModelIntegrationData;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.service.IntegrationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    public static final String QUESTIONNAIRE_MODELS_JSON = "questionnaireModels.json";

    @Override
    public List<IntegrationResultUnitDto> build(String campaignId, ZipFile integrationZipFile) {
        return buildQuestionnaireModels(campaignId, integrationZipFile);
    }

    private List<IntegrationResultUnitDto> buildQuestionnaireModels(String campaignId, ZipFile zf) {
        try {
            schemaComponent.throwExceptionIfJsonDataFileNotValid(zf, QUESTIONNAIRE_MODELS_JSON, SchemaType.QUESTIONNAIRE_INTEGRATION);
        } catch (IntegrationValidationException ex) {
            return List.of(ex.getResultError());
        }

        ZipEntry zipQuestionnairesFile = zf.getEntry(QUESTIONNAIRE_MODELS_JSON);
        List<QuestionnaireModelItem> questionnaireModelItems;

        try {
            questionnaireModelItems = mapper.readValue(zf.getInputStream(zipQuestionnairesFile), new TypeReference<List<QuestionnaireModelItem>>(){});
        } catch (JacksonException _) {
            IntegrationResultUnitDto resultError = IntegrationResultUnitDto.integrationResultUnitError(
                    null,
                    IntegrationResultLabel.JSON_PARSING_ERROR.formatted(QUESTIONNAIRE_MODELS_JSON));
            return List.of(resultError);
        } catch (IOException _) {
            IntegrationResultUnitDto resultError = IntegrationResultUnitDto.integrationResultUnitError(
                    null,
                    IntegrationResultLabel.ZIP_PARSING_ERROR.formatted(zf.getName()));
            return List.of(resultError);
        }

        List<IntegrationResultUnitDto> results = new ArrayList<>();
        for(QuestionnaireModelItem questionnaireModelItem : questionnaireModelItems) {
            try {
                ObjectNode qmValue = readQuestionnaireStream(questionnaireModelItem, zf);
                results.addAll(buildQuestionnaireModel(campaignId, questionnaireModelItem, qmValue));
            } catch (IntegrationValidationException ex) {
                results.add(ex.getResultError());
            }
        }
        return results;
    }

    private List<IntegrationResultUnitDto> buildQuestionnaireModel(String qmCampaignId, QuestionnaireModelItem questionnaireModelItem, ObjectNode qmValue) {
        QuestionnaireModelIntegrationData questionnaire = new QuestionnaireModelIntegrationData(questionnaireModelItem.id(),
                qmCampaignId,
                questionnaireModelItem.label(),
                qmValue,
                questionnaireModelItem.requiredNomenclatures());
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
        } catch (JacksonException | IOException _) {
            log.info("Could not parse json in file {}", questionnaireModelItem.filename());
            throw new IntegrationValidationException(IntegrationResultUnitDto.integrationResultUnitError(
                    questionnaireModelItem.id(),
                    String.format(IntegrationResultLabel.JSON_PARSING_ERROR, questionnaireModelItem.filename()))
            );
        }
    }

    private InputStream getQuestionnaireInputStream(ZipFile zf, QuestionnaireModelItem questionnaireModelItem) throws IntegrationValidationException, IOException {
        String qmFileName = questionnaireModelItem.filename();
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
