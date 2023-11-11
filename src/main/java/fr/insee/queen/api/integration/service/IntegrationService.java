package fr.insee.queen.api.integration.service;

import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.Nomenclature;
import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;
import fr.insee.queen.api.integration.service.model.IntegrationResult;

import java.util.List;


public interface IntegrationService {
    IntegrationResult create(Campaign campaign);

    IntegrationResult create(Nomenclature nomenclature);

    List<IntegrationResult> create(QuestionnaireModel questionnaire);
}
