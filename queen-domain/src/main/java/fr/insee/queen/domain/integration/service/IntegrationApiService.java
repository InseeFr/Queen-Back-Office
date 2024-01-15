package fr.insee.queen.domain.integration.service;

import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import fr.insee.queen.domain.campaign.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class IntegrationApiService implements IntegrationService {
    private final CampaignService campaignService;
    private final CampaignExistenceService campaignExistenceService;
    private final QuestionnaireModelExistenceService questionnaireModelExistenceService;
    private final QuestionnaireModelService questionnaireModelService;
    private final NomenclatureService nomenclatureService;

    @Override
    public IntegrationResult create(Campaign campaign) {
        String id = campaign.getId();

        if (campaignExistenceService.existsById(id)) {
            log.info("Updating campaign {}", id);
            campaignService.updateCampaign(campaign);
            return new IntegrationResult(id, IntegrationStatus.UPDATED, null);
        }

        log.info("Creating campaign {}", id);
        campaignService.createCampaign(campaign);
        return new IntegrationResult(id, IntegrationStatus.CREATED, null);
    }

    @Override
    public IntegrationResult create(Nomenclature nomenclature) {
        String nomenclatureId = nomenclature.id();
        if (nomenclatureService.existsById(nomenclatureId)) {
            log.info("Nomenclature {} already exists", nomenclatureId);
            return new IntegrationResult(nomenclatureId, IntegrationStatus.ERROR,
                    String.format(IntegrationResultLabel.NOMENCLATURE_ALREADY_EXISTS, nomenclatureId));
        }
        log.info("Creating nomenclature {}", nomenclatureId);
        nomenclatureService.saveNomenclature(nomenclature);
        return new IntegrationResult(nomenclatureId, IntegrationStatus.CREATED, null);
    }

    @Override
    public List<IntegrationResult> create(QuestionnaireModel questionnaire) {
        String campaignId = questionnaire.getCampaignId();
        String qmId = questionnaire.getId();
        boolean hasError = false;

        List<IntegrationResult> results = new ArrayList<>();
        // Checking if campaign exists
        if (!campaignExistenceService.existsById(campaignId)) {
            hasError = true;
            log.info("Cannot create Questionnaire model {}, campaign {} does not exist", qmId, campaignId);
            results.add(new IntegrationResult(
                    qmId,
                    IntegrationStatus.ERROR,
                    String.format(IntegrationResultLabel.CAMPAIGN_DO_NOT_EXIST, campaignId)));
        }

        // Checking if required nomenclatures exist
        for (String nomenclatureId : questionnaire.getRequiredNomenclatureIds()) {
            if (!nomenclatureService.existsById(nomenclatureId)) {
                hasError = true;
                log.info("Cannot create Questionnaire model {}, nomenclature {} does not exist", qmId, nomenclatureId);
                results.add(new IntegrationResult(
                        qmId,
                        IntegrationStatus.ERROR,
                        String.format(IntegrationResultLabel.NOMENCLATURE_DO_NOT_EXIST, nomenclatureId)
                ));
            }
        }

        if (hasError) {
            return results;
        }

        if (questionnaireModelExistenceService.existsById(qmId)) {
            log.info("QuestionnaireModel {} already exists", qmId);
            questionnaireModelService.updateQuestionnaire(questionnaire);
            results.add(new IntegrationResult(qmId, IntegrationStatus.UPDATED, null));
            return results;
        }

        log.info("Creating questionnaire model {}", qmId);
        questionnaireModelService.createQuestionnaire(questionnaire);
        results.add(new IntegrationResult(qmId, IntegrationStatus.CREATED, null));
        return results;
    }
}
