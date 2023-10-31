package fr.insee.queen.api.controller.integration.component.creator;

import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.api.dto.input.QuestionnaireModelIntegrationInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationStatus;
import fr.insee.queen.api.service.campaign.CampaignExistenceService;
import fr.insee.queen.api.service.questionnaire.NomenclatureService;
import fr.insee.queen.api.service.questionnaire.QuestionnaireModelExistenceService;
import fr.insee.queen.api.service.questionnaire.QuestionnaireModelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class QuestionnaireCreator {

    private final CampaignExistenceService campaignExistenceService;
    private final QuestionnaireModelExistenceService questionnaireModelExistenceService;
    private final QuestionnaireModelService questionnaireModelService;
    private final NomenclatureService nomenclatureService;

    public List<IntegrationResultUnitDto> create(List<QuestionnaireModelIntegrationInputDto> questionnaires) {

        List<IntegrationResultUnitDto> integrationResults = new ArrayList<>();
        for (QuestionnaireModelIntegrationInputDto questionnaire : questionnaires) {
            try {
                IntegrationResultUnitDto result = create(questionnaire);
                integrationResults.add(result);
            } catch (IntegrationValidationException ex) {
                integrationResults.add(ex.resultError());
            }
        }
        return integrationResults;
    }

    public IntegrationResultUnitDto create(QuestionnaireModelIntegrationInputDto questionnaire) throws IntegrationValidationException {
        String campaignId = questionnaire.campaignId();
        String qmId = questionnaire.idQuestionnaireModel();

        // Checking if campaign exists
        if(!campaignExistenceService.existsById(campaignId)) {
            log.info("Could not create Questionnaire model {}, campaign {} does not exist", qmId, campaignId);
            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    qmId,
                    "The campaign '" + campaignId + "' does not exist")
            );
        }

        // Checking if required nomenclatures exist
        for(String nomenclatureId : questionnaire.requiredNomenclatureIds()) {
            if(!nomenclatureService.existsById(nomenclatureId)) {
                log.info("Could not create Questionnaire model {}, nomenclature {} does not exist", qmId, nomenclatureId);
                throw new IntegrationValidationException((new IntegrationResultErrorUnitDto(
                        qmId,
                        "The nomenclature '" + nomenclatureId + "' does not exist")
                ));
            }
        }

        IntegrationStatus status;
        if(questionnaireModelExistenceService.existsById(qmId)) {
            log.info("QuestionnaireModel {} already exists", qmId);
            questionnaireModelService.updateQuestionnaire(QuestionnaireModelIntegrationInputDto.toModel(questionnaire));
            status = IntegrationStatus.UPDATED;
        }
        else {
            log.info("Creating questionnaire model {}", qmId);
            questionnaireModelService.createQuestionnaire(QuestionnaireModelIntegrationInputDto.toModel(questionnaire));
            status = IntegrationStatus.CREATED;
        }

        return new IntegrationResultUnitDto(
                    qmId,
                    status,
                    null);
    }
}
