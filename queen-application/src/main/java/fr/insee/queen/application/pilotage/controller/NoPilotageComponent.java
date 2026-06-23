package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.domain.group.service.GroupService;
import fr.insee.queen.domain.pilotage.model.PilotageGroup;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnExpression(value = "${feature.oidc.enabled} == false or ${feature.pilotage.enabled} == false")
@RequiredArgsConstructor
@Component
public class NoPilotageComponent implements PilotageComponent {
    private final InterrogationService interrogationService;
    private final GroupService groupService;

    @Override
    public boolean isClosed(String groupId) {
        return true;
    }

    @Override
    public List<InterrogationSummary> getInterrogations(String groupId) {
        return interrogationService.findSummariesByGroupId(groupId);
    }

    @Override
    public List<PilotageGroup> getInterviewerGroups() {
        return groupService.getAllGroups().stream()
                .map(group -> new PilotageGroup(group.getId(), group.getQuestionnaireIds().stream().toList()))
                .toList();
    }

    @Override
    public List<Interrogation> getInterviewerInterrogations() {
        return interrogationService.findAllInterrogations();
    }

    @Override
    public void checkHabilitations(String interrogationId, PilotageRole... roles) {
        interrogationService.throwExceptionIfInterrogationNotExist(interrogationId);
    }
}
