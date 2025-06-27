package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.domain.campaign.service.CampaignService;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
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
    private final CampaignService campaignService;

    @Override
    public boolean isClosed(String campaignId) {
        return true;
    }

    @Override
    public List<InterrogationSummary> getInterrogationsByCampaign(String campaignId) {
        return interrogationService.findSummariesByCampaignId(campaignId);
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns() {
        return campaignService.getAllCampaigns().stream()
                .map(campaign -> new PilotageCampaign(campaign.getId(), campaign.getQuestionnaireIds().stream().toList()))
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
