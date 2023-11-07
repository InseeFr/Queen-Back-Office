package fr.insee.queen.api.service.integration;

import fr.insee.queen.api.controller.integration.component.IntegrationResultLabel;
import fr.insee.queen.api.dto.input.CampaignIntegrationInputDto;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.input.QuestionnaireModelIntegrationInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultSuccessUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import fr.insee.queen.api.service.campaign.CampaignExistenceService;
import fr.insee.queen.api.service.campaign.CampaignService;
import fr.insee.queen.api.service.questionnaire.NomenclatureService;
import fr.insee.queen.api.service.questionnaire.QuestionnaireModelExistenceService;
import fr.insee.queen.api.service.questionnaire.QuestionnaireModelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class IntegrationService {
    private final CampaignService campaignService;
    private final CampaignExistenceService campaignExistenceService;
    private final QuestionnaireModelExistenceService questionnaireModelExistenceService;
    private final QuestionnaireModelService questionnaireModelService;
    private final NomenclatureService nomenclatureService;


    public Pair<Optional<String>, IntegrationResultUnitDto> create(CampaignIntegrationInputDto campaign) {
        String id = campaign.id();

        if(campaignExistenceService.existsById(id)) {
            log.info("Updating campaign {}", id);
            campaignService.updateCampaign(CampaignIntegrationInputDto.toModel(campaign));
            return Pair.of(Optional.of(id), IntegrationResultSuccessUnitDto.integrationResultUnitUpdated(id));
        }

        log.info("Creating campaign {}", id);
        campaignService.createCampaign(CampaignIntegrationInputDto.toModel(campaign));
        return Pair.of(Optional.of(id), IntegrationResultSuccessUnitDto.integrationResultUnitCreated(id));
    }

    public IntegrationResultUnitDto create(NomenclatureInputDto nomenclature) {
        String nomenclatureId = nomenclature.id();
        if(nomenclatureService.existsById(nomenclatureId)) {
            log.info("Nomenclature {} already exists", nomenclatureId);
            return new IntegrationResultErrorUnitDto(
                    nomenclatureId,
                    String.format(IntegrationResultLabel.NOMENCLATURE_ALREADY_EXISTS, nomenclatureId));
        }
        log.info("Creating nomenclature {}", nomenclatureId);
        nomenclatureService.saveNomenclature(nomenclature);
        return IntegrationResultSuccessUnitDto.integrationResultUnitCreated(nomenclature.id());
    }

    public List<IntegrationResultUnitDto> create(QuestionnaireModelIntegrationInputDto questionnaire) {
        String campaignId = questionnaire.campaignId();
        String qmId = questionnaire.idQuestionnaireModel();
        boolean hasError = false;

        List<IntegrationResultUnitDto> results = new ArrayList<>();
        // Checking if campaign exists
        if(!campaignExistenceService.existsById(campaignId)) {
            hasError = true;
            log.info("Cannot create Questionnaire model {}, campaign {} does not exist", qmId, campaignId);
            results.add(new IntegrationResultErrorUnitDto(
                    qmId,
                    String.format(IntegrationResultLabel.CAMPAIGN_DO_NOT_EXIST, campaignId)));
        }

        // Checking if required nomenclatures exist
        for(String nomenclatureId : questionnaire.requiredNomenclatureIds()) {
            if(!nomenclatureService.existsById(nomenclatureId)) {
                hasError = true;
                log.info("Cannot create Questionnaire model {}, nomenclature {} does not exist", qmId, nomenclatureId);
                results.add(new IntegrationResultErrorUnitDto(
                        qmId,
                        String.format(IntegrationResultLabel.NOMENCLATURE_DO_NOT_EXIST, nomenclatureId)
                ));
            }
        }

        if(hasError) {
            return results;
        }

        if(questionnaireModelExistenceService.existsById(qmId)) {
            log.info("QuestionnaireModel {} already exists", qmId);
            questionnaireModelService.updateQuestionnaire(QuestionnaireModelIntegrationInputDto.toModel(questionnaire));
            results.add(IntegrationResultSuccessUnitDto.integrationResultUnitUpdated(qmId));
            return results;
        }

        log.info("Creating questionnaire model {}", qmId);
        questionnaireModelService.createQuestionnaire(QuestionnaireModelIntegrationInputDto.toModel(questionnaire));
        results.add(IntegrationResultSuccessUnitDto.integrationResultUnitCreated(qmId));
        return results;
    }
}
