package fr.insee.queen.api.controller.utils;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.AuthEnumProperties;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.exception.HabilitationException;
import fr.insee.queen.api.service.HabilitationService;
import fr.insee.queen.api.service.SurveyUnitService;
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
public class HabilitationComponent {
    private final HabilitationService habilitationService;

    private final ApplicationProperties applicationProperties;
    private final AuthenticationHelper authHelper;
    private final SurveyUnitService surveyUnitService;

    @Value("${application.pilotage.integration-override}")
    private final String integrationOverride;

    public void checkHabilitations(Authentication auth, String surveyUnitId, String... roles) {

        SurveyUnitHabilitationDto surveyUnit = 	surveyUnitService.getSurveyUnitWithCampaignById(surveyUnitId);

        if(integrationOverride != null && integrationOverride.equals("true")) {
            return;
        }

        if(applicationProperties.auth().equals(AuthEnumProperties.NOAUTH)) {
            return;
        }

        if(!auth.isAuthenticated()) {
            // not authenticated user cannot have habilitation
            throw new HabilitationException();
        }
        List<String> userRoles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        habilitationService.checkHabilitations(authHelper.getUserId(auth), userRoles, surveyUnit, authHelper.getAuthToken(auth), roles);
    }
}
