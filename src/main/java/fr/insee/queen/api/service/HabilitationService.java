package fr.insee.queen.api.service;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.configuration.properties.AuthEnumProperties;
import fr.insee.queen.api.configuration.properties.RoleProperties;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.exception.HabilitationException;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class HabilitationService {
	@Value("${application.pilotage.integration-override}")
	private final String integrationOverride;
	private final ApplicationProperties applicationProperties;
	private final RoleProperties roleProperties;
	private final SurveyUnitRepository surveyUnitRepository;
	private final PilotageApiService pilotageApiService;

	public String getUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return switch (applicationProperties.auth()) {
			case BASIC -> {
				Object basic = authentication.getPrincipal();
				if (basic instanceof UserDetails userDetails) {
					yield userDetails.getUsername();
				}
				yield basic.toString();
			}
			case KEYCLOAK -> {
				if(authentication.getCredentials() instanceof Jwt jwt) {
					yield jwt.getClaims().get("preferred_username").toString();
				}
				yield Constants.GUEST;
			}
			default -> Constants.GUEST;
		};
	}

	public void checkHabilitations(HttpServletRequest request, String surveyUnitId, String... roles){
		log.info("Check habilitation of user {} with role {} to access survey-unit {} ", request.getRemoteUser(), roles, surveyUnitId);

		SurveyUnitHabilitationDto surveyUnit = 	surveyUnitRepository.findWithCampaignById(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId)));

		if(integrationOverride != null && integrationOverride.equals("true")) {
			return;
		}

		String userId = getUserId();
		if(userId.equals(Constants.GUEST)) {
			if(applicationProperties.auth().equals(AuthEnumProperties.NOAUTH)) {
				return;
			}
			// Guest user cannot have habilitation
			throw new HabilitationException();
		}

		if(request.isUserInRole(roleProperties.admin())||request.isUserInRole(roleProperties.webclient())) {
			return;
		}

		for(String role : roles) {
			String idep = getIdepFromToken(request);
			if(pilotageApiService.hasHabilitation(request, surveyUnit, role, idep)) {
				return;
			}
		}
		throw new HabilitationException();
	}
	
	public String getIdepFromToken(HttpServletRequest request) {
		if (!applicationProperties.auth().equals(AuthEnumProperties.NOAUTH)) {
			return request.getRemoteUser();
		}
		return "";
	}
}
