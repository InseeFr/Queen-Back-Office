package fr.insee.queen.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.service.ParadataEventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	 * @return {@link HttpStatus}
	 */
	@Operation(summary = "Add a ParadataEvent")
	@PostMapping(path = "/paradata")
	public HttpStatus updateSurveyUnit(@RequestBody @NonNull JsonNode paradataValue, Authentication auth) {
		String paradataSurveyUnitIdParameter = "idSU";
		log.info("POST ParadataEvent");
		if(!paradataValue.has(paradataSurveyUnitIdParameter)) {
			throw new IllegalArgumentException("Paradata does not contain the survey unit id");
		}

		JsonNode surveyUnitNode = paradataValue.get(paradataSurveyUnitIdParameter);
		if(!surveyUnitNode.isTextual() || surveyUnitNode.textValue() == null) {
			throw new IllegalArgumentException("Paradata does not contain the survey unit id");
		}

		habilitationComponent.checkHabilitations(auth, surveyUnitNode.textValue(), Constants.INTERVIEWER);
		paradataEventService.save(paradataValue.toString());
		return HttpStatus.OK;
	}
}
