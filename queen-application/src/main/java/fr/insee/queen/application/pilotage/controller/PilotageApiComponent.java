package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.pilotage.service.PilotageService;
import fr.insee.queen.domain.pilotage.service.exception.HabilitationException;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
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
    private final SurveyUnitService surveyUnitService;

    @Override
    public boolean isClosed(String campaignId) {
        String userToken = authHelper.getUserToken();
        return pilotageService.isClosed(campaignId, userToken);
    }

    @Override
    public List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId) {
        String authToken = authHelper.getUserToken();
        return pilotageService.getSurveyUnitsByCampaign(campaignId, authToken);
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns() {
        String authToken = authHelper.getUserToken();
        List<PilotageCampaign> campaigns = pilotageService.getInterviewerCampaigns(authToken);
        log.info("{} campaign(s) found", campaigns.size());
        return campaigns;
    }

    @Override
    public List<SurveyUnit> getInterviewerSurveyUnits() {
        String authToken = authHelper.getUserToken();
        return pilotageService.getInterviewerSurveyUnits(authToken);
    }

    @Override
    public void checkHabilitations(String surveyUnitId, PilotageRole... rolesToCheck) {
        SurveyUnitSummary surveyUnit = surveyUnitService.getSurveyUnitWithCampaignById(surveyUnitId);
        Authentication auth = authHelper.getAuthenticationPrincipal();

        if (!auth.isAuthenticated()) {
            // anonymous user cannot have habilitation
            throw new HabilitationException("Habilitation denied: user is not authenticated");
        }

        List<String> userRoles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String userId = authHelper.getUserId();
        if (userRoles.contains("ROLE_ADMIN") || userRoles.contains("ROLE_WEBCLIENT")) {
            log.info("Habilitation granted: user {} is admin", userId);
            return;
        }


        String userToken = authHelper.getUserToken();
        for (PilotageRole roleToCheck : rolesToCheck) {
            if (pilotageService.hasHabilitation(surveyUnit, roleToCheck, userId, userToken)) {
                log.info("Habilitation granted: user {} has access to survey-unit {} with role {}", userId, surveyUnit.id(), roleToCheck);
                return;
            }
        }
        throw new HabilitationException(String.format("Habilitation denied: user %s has not access to survey-unit %s with roles %s", userId, surveyUnit.id(), Arrays.toString(rolesToCheck)));
    }
}
