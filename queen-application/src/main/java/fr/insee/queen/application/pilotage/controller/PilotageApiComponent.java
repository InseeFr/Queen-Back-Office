package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.pilotage.service.PilotageService;
import fr.insee.queen.domain.pilotage.service.exception.HabilitationException;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@ConditionalOnExpression(value = "${feature.oidc.enabled} != false and ${feature.pilotage.enabled} != false")
@Component
@AllArgsConstructor
@Slf4j
public class PilotageApiComponent implements PilotageComponent {
    private final PilotageService pilotageService;
    private final AuthenticationHelper authHelper;
    private final InterrogationService interrogationService;

    @Override
    public boolean isClosed(String campaignId) {
        return pilotageService.isClosed(campaignId);
    }

    @Override
    public List<InterrogationSummary> getInterrogationsByCampaign(String campaignId) {
        return pilotageService.getInterrogationsByCampaign(campaignId);
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns() {
        List<PilotageCampaign> campaigns = pilotageService.getInterviewerCampaigns();
        log.info("{} campaign(s) found", campaigns.size());
        return campaigns;
    }

    @Override
    public List<Interrogation> getInterviewerInterrogations() {
        return pilotageService.getInterviewerInterrogations();
    }

    @Override
    public void checkHabilitations(String interrogationId, PilotageRole... rolesToCheck) {
        InterrogationSummary interrogation = interrogationService.getSummaryById(interrogationId);
        Authentication auth = authHelper.getAuthenticationPrincipal();

        List<String> userRoles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String userId = auth.getName();
        if (userRoles.contains("ROLE_ADMIN") || userRoles.contains("ROLE_WEBCLIENT")) {
            log.info("Habilitation granted: user {} is admin", userId);
            return;
        }

        for (PilotageRole roleToCheck : rolesToCheck) {
            if (pilotageService.hasHabilitation(interrogation, roleToCheck, userId)) {
                log.info("Habilitation granted: user {} has access to interrogation {} with role {}", userId, interrogation.id(), roleToCheck);
                return;
            }
        }
        throw new HabilitationException(String.format("Habilitation denied: user %s has not access to interrogation %s with roles %s", userId, interrogation.id(), Arrays.toString(rolesToCheck)));
    }
}
