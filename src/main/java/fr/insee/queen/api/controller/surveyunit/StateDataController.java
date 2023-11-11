package fr.insee.queen.api.controller.surveyunit;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.utils.HabilitationComponent;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.input.StateDataInputDto;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitOkNokDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitWithStateDto;
import fr.insee.queen.api.service.pilotage.PilotageRole;
import fr.insee.queen.api.service.surveyunit.StateDataService;
import fr.insee.queen.api.service.surveyunit.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* Handle the data state of a survey unit.
*/
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class StateDataController {
	private final StateDataService stateDataService;
	private final SurveyUnitService surveyUnitService;
	private final HabilitationComponent habilitationComponent;
	
	/**
	* Retrieve the data associated of a survey unit
	* 
	* @param surveyUnitId the id of the survey unit
	* @param auth authentication object
	* @return {@link StateDataDto} the data associated to the survey unit
	*/
	@Operation(summary = "Get state-data for a survey unit")
	@GetMapping(path = "/survey-unit/{id}/state-data")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public StateDataDto getStateDataBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId,
												  Authentication auth){
		log.info("GET statedata for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER);
		return stateDataService.getStateData(surveyUnitId);
	}
	
	/**
	* Update the state-data associated to the survey unit
	* 
	* @param stateDataInputDto	the value to update
	* @param surveyUnitId	the id of reporting unit
	* @param auth authentication object
	*/
	@Operation(summary = "Update state-data for a survey unit")
	@PutMapping(path = "/survey-unit/{id}/state-data")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public void setStateData(@IdValid @PathVariable(value = "id") String surveyUnitId,
								   @Valid @RequestBody StateDataInputDto stateDataInputDto,
								   Authentication auth) {
		log.info("PUT statedata for reporting unit with id {}", surveyUnitId);
		habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER);
		stateDataService.updateStateData(surveyUnitId, StateDataInputDto.toModel(stateDataInputDto));
	}

	/**
	 * Retrieve the state-data list of searched survey units
	 *
	 * @param surveyUnitIdsToSearch the ids to search
	 * @return {@link SurveyUnitOkNokDto} the state-data associated for found survey units, and the list of non found survey units
	 */
	@Operation(summary = "Get state-data for all survey-units defined in request body ")
	@PostMapping(path = "survey-units/state-data")
	@PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
	public SurveyUnitOkNokDto getStateDataBySurveyUnits(@NotEmpty @RequestBody List<String> surveyUnitIdsToSearch){
		List<SurveyUnitWithStateDto> surveyUnitsFound = surveyUnitService.findWithStateByIds(surveyUnitIdsToSearch);
		List<String> surveyUnitIdsFound = surveyUnitsFound.stream().map(SurveyUnitWithStateDto::id).toList();
		List<SurveyUnitDto> surveyUnitsNOK = surveyUnitIdsToSearch.stream()
				.filter(surveyUnitIdToSearch -> !surveyUnitIdsFound.contains(surveyUnitIdToSearch))
				.map(SurveyUnitDto::createSurveyUnitNOKDto)
				.toList();
		List<SurveyUnitDto> surveyUnitsOK = surveyUnitsFound.stream()
				.map(su -> SurveyUnitDto.createSurveyUnitOKDtoWithStateData(su.id(), su.stateData()))
				.toList();
		return new SurveyUnitOkNokDto(surveyUnitsOK, surveyUnitsNOK);
	}
}
