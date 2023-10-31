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
* DataController is the Controller using to manage datas
* 
* @author Claudel Benjamin
* 
*/
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class StateDataController {
	/**
	* The data repository using to access to table 'data' in DB 
	*/
	private final StateDataService stateDataService;
	/**
	* The reporting unit repository using to access to table 'reporting_unit' in DB 
	*/
	private final SurveyUnitService surveyUnitService;
	private final HabilitationComponent habilitationComponent;
	
	/**
	* This method is using to get the data associated to a specific reporting unit 
	* 
	* @param surveyUnitId the id of reporting unit
	* @return {@link StateDataDto} the data associated to the reporting unit
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
	* This method is using to update the state-data associated to a specific reporting unit
	* 
	* @param stateDataInputDto	the value to update
	* @param surveyUnitId	the id of reporting unit
	*
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
