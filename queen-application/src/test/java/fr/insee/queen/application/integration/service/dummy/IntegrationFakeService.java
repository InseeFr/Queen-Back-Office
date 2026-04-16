package fr.insee.queen.application.integration.service.dummy;


import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import fr.insee.queen.domain.integration.service.IntegrationService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class IntegrationFakeService implements IntegrationService {
    private Campaign campaignCreated = null;
    @Setter
    private boolean shouldFail = false;

    @Override
    public IntegrationResult create(Campaign campaign) {
        campaignCreated = campaign;
        if (shouldFail) {
            return new IntegrationResult(campaign.getId(), IntegrationStatus.ERROR, "Failed to create campaign");
        }
        return new IntegrationResult(campaign.getId(), IntegrationStatus.CREATED, null);
    }

    @Override
    public IntegrationResult create(Nomenclature nomenclature) {
        if (shouldFail) {
            return new IntegrationResult(nomenclature.id(), IntegrationStatus.ERROR, "Failed to create nomenclature");
        }
        return new IntegrationResult(nomenclature.id(), IntegrationStatus.CREATED, null);
    }

    @Override
    public List<IntegrationResult> create(QuestionnaireModel questionnaire) {
        if (shouldFail) {
            return List.of(new IntegrationResult(questionnaire.getId(), IntegrationStatus.ERROR, "Failed to create questionnaire"));
        }
        return List.of(new IntegrationResult(questionnaire.getId(), IntegrationStatus.CREATED, null));
    }
}
