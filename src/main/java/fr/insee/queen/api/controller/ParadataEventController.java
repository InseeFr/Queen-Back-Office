package fr.insee.queen.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.service.ParadataEventService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * ParadataEnventController is the Controller using to manage paradata events
 * entity
 * 
 * @author Corcaud Samuel
 * 
 */
@RestController
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
public class ParadataEventController {
	
	private final ParadataEventService paradataEventService;

	private final HabilitationComponent habilitationComponent;

	/**
	 * This method is used to save a pardata event
	 * 
	 * @param paradataValue paradata value
	 */
	@Operation(summary = "Add a ParadataEvent")
	@PostMapping(path = "/paradata")
	@PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES + "||" + AuthorityRole.INTERVIEWER)
	@ResponseStatus(HttpStatus.CREATED)
	public void updateSurveyUnit(@NotNull @RequestBody ObjectNode paradataValue, Authentication auth) {
		String paradataSurveyUnitIdParameter = "idSU";
		log.info("POST ParadataEvent");
		if(!paradataValue.has(paradataSurveyUnitIdParameter)) {
			throw new IllegalArgumentException("Paradata does not contain the survey unit id");
		}

		JsonNode surveyUnitNode = paradataValue.get(paradataSurveyUnitIdParameter);
		if(!surveyUnitNode.isTextual() || surveyUnitNode.textValue() == null) {
			throw new IllegalArgumentException("Paradata does not contain the survey unit id");
		}

		String surveyUnitId = surveyUnitNode.textValue();
		habilitationComponent.checkHabilitations(auth, surveyUnitId, Constants.INTERVIEWER);
		paradataEventService.createParadataEvent(surveyUnitId, paradataValue.toString());
	}
}
