package fr.insee.queen.application.integration.service.dummy;


import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.Nomenclature;
import fr.insee.queen.domain.group.model.QuestionnaireModel;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import fr.insee.queen.domain.integration.service.IntegrationService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class IntegrationFakeService implements IntegrationService {
    private List<Group> groupsCreated = new ArrayList<>();

    @Override
    public IntegrationResult create(Group group) {
        groupsCreated.add(group);
        return new IntegrationResult(group.getId(), IntegrationStatus.CREATED, null);
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
