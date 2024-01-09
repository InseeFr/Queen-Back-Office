package fr.insee.queen.domain.integration.service;

import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.integration.model.IntegrationResult;

import java.util.List;


public interface IntegrationService {
    IntegrationResult create(Campaign campaign);

    IntegrationResult create(Nomenclature nomenclature);

    List<IntegrationResult> create(QuestionnaireModel questionnaire);
}
