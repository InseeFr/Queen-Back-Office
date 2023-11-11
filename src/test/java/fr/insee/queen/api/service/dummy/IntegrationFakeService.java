package fr.insee.queen.api.service.dummy;

import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.Nomenclature;
import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;
import fr.insee.queen.api.integration.service.IntegrationService;
import fr.insee.queen.api.integration.service.model.IntegrationResult;
import fr.insee.queen.api.integration.service.model.IntegrationStatus;

import java.util.List;

public class IntegrationFakeService implements IntegrationService {
    @Override
    public IntegrationResult create(Campaign campaign) {
        return new IntegrationResult(campaign.id(), IntegrationStatus.CREATED, null);
    }

    @Override
    public IntegrationResult create(Nomenclature nomenclature) {
        return new IntegrationResult(nomenclature.id(), IntegrationStatus.CREATED, null);
    }

    @Override
    public List<IntegrationResult> create(QuestionnaireModel questionnaire) {
        return List.of(new IntegrationResult(questionnaire.id(), IntegrationStatus.CREATED, null));
    }
}
