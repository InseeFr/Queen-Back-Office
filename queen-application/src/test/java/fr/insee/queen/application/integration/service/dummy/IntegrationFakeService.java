package fr.insee.queen.application.integration.service.dummy;


import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import fr.insee.queen.domain.integration.service.IntegrationService;

import java.util.List;

public class IntegrationFakeService implements IntegrationService {
    @Override
    public IntegrationResult create(Campaign campaign) {
        return new IntegrationResult(campaign.getId(), IntegrationStatus.CREATED, null);
    }

    @Override
    public IntegrationResult create(Nomenclature nomenclature) {
        return new IntegrationResult(nomenclature.id(), IntegrationStatus.CREATED, null);
    }

    @Override
    public List<IntegrationResult> create(QuestionnaireModel questionnaire) {
        return List.of(new IntegrationResult(questionnaire.getId(), IntegrationStatus.CREATED, null));
    }
}
