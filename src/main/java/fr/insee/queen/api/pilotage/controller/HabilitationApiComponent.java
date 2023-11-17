package fr.insee.queen.api.pilotage.controller;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.AuthEnumProperties;
import fr.insee.queen.api.pilotage.service.exception.HabilitationException;
import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.PilotageService;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import fr.insee.queen.api.web.authentication.AuthenticationHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class HabilitationApiComponent implements HabilitationComponent {
    private final PilotageService pilotageService;
    private final ApplicationProperties applicationProperties;
    private final AuthenticationHelper authHelper;
    private final SurveyUnitService surveyUnitService;

    @Value("${application.pilotage.integration-override}")
    private final String integrationOverride;

    @Override
    public void checkHabilitations(String surveyUnitId, PilotageRole... rolesToCheck) {

        SurveyUnitSummary surveyUnit = surveyUnitService.getSurveyUnitWithCampaignById(surveyUnitId);

        if (integrationOverride != null && integrationOverride.equals("true")) {
            return;
        }

        if (applicationProperties.auth().equals(AuthEnumProperties.NOAUTH)) {
            return;
        }

        Authentication auth = authHelper.getAuthenticationPrincipal();
        if (!auth.isAuthenticated()) {
            // not authenticated user cannot have habilitation
            throw new HabilitationException();
        }

        List<String> userRoles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (userRoles.contains("ROLE_ADMIN") || userRoles.contains("ROLE_WEBCLIENT")) {
            return;
        }

        String userId = authHelper.getUserId();
        log.info("Check habilitation of user {} with role {} to access survey-unit {} ", userId, rolesToCheck, surveyUnit.id());
        String userToken = authHelper.getUserToken();
        for (PilotageRole roleToCheck : rolesToCheck) {
            if (pilotageService.hasHabilitation(surveyUnit, roleToCheck, userId, userToken)) {
                return;
            }
        }
        throw new HabilitationException();
    }
}
