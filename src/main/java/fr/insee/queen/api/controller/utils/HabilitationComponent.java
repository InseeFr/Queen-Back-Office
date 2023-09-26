package fr.insee.queen.api.controller.utils;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.AuthEnumProperties;
import fr.insee.queen.api.exception.HabilitationException;
import fr.insee.queen.api.service.HabilitationService;
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
    private HabilitationService habilitationService;

    private ApplicationProperties applicationProperties;

    private AuthenticationHelper authHelper;

    @Value("${application.pilotage.integration-override}")
    private final String integrationOverride;

    public void checkHabilitations(Authentication auth, String surveyUnitId, String... roles) {
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
        habilitationService.checkHabilitations(authHelper.getUserId(auth), userRoles, surveyUnitId, authHelper.getAuthToken(auth), roles);
    }
}
