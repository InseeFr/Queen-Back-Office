package fr.insee.queen.domain.integration.service;

import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.Nomenclature;
import fr.insee.queen.domain.group.model.QuestionnaireModel;
import fr.insee.queen.domain.integration.model.IntegrationResult;

import java.util.List;


public interface IntegrationService {
    IntegrationResult create(Group group);

    IntegrationResult create(Nomenclature nomenclature);

    List<IntegrationResult> create(QuestionnaireModel questionnaire);
}
