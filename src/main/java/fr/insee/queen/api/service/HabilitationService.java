package fr.insee.queen.api.service;

import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.exception.HabilitationException;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class HabilitationService {
	private final SurveyUnitRepository surveyUnitRepository;
	private final PilotageApiService pilotageApiService;

	public void checkHabilitations(String username, List<String> userRoles, String surveyUnitId, String authToken, String... rolesToCheck){
		log.info("Check habilitation of user {} with role {} to access survey-unit {} ", username, rolesToCheck, surveyUnitId);

		SurveyUnitHabilitationDto surveyUnit = 	surveyUnitRepository.findWithCampaignById(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("Survey unit %s was not found", surveyUnitId)));

		if(userRoles.contains("ROLE_ADMIN") || userRoles.contains("ROLE_WEBCLIENT")) {
			return;
		}

		for(String roleToCheck : rolesToCheck) {
			if (pilotageApiService.hasHabilitation(surveyUnit, roleToCheck, username, authToken)) {
				return;
			}
		}
		throw new HabilitationException();
	}
}
