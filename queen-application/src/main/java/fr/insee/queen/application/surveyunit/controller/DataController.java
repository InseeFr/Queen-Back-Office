package fr.insee.queen.application.surveyunit.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.surveyunit.controller.exception.ConflictException;
import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.application.web.validation.json.JsonValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.domain.surveyunit.service.DataService;
import fr.insee.queen.domain.surveyunit.service.StateDataService;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * These endpoints handle the questionnaire form data of a survey unit
 */
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class DataController {
    private final DataService dataService;
    private final PilotageComponent pilotageComponent;
    private final StateDataService stateDataService;
    private final SurveyUnitService surveyUnitService;
    private final AuthenticationHelper authenticationUserHelper;

    /**
     * Retrieve the questionnaire form data of a survey unit
     *
     * @param surveyUnitId the id of reporting unit
     * @return {@link String} the questionnaire form data of a survey unit
     */
    @Operation(summary = "Get data for a survey unit")
    @GetMapping(path = "/survey-unit/{id}/data")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(ref = SchemaType.Names.DATA))})
    public ObjectNode getDataBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        SurveyUnitSummary surveyUnitSummary = surveyUnitService.getSummaryById(surveyUnitId);

        // if campaign sensitivity is OFF, return data
        if(surveyUnitSummary.campaign().getSensitivity().equals(CampaignSensitivity.NORMAL)) {
            return dataService.getData(surveyUnitId);
        }

        // here, campaign sensitivity is ON !

        // admin can see everything
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.ADMIN, AuthorityRoleEnum.WEBCLIENT)){
            return dataService.getData(surveyUnitId);
        }

        // interviewer retrieve the dto with filled or empty data
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.SURVEY_UNIT)){
            Optional<StateDataType> validatedState = stateDataService
                    .findStateData(surveyUnitId)
                    .map(StateData::state)
                    .filter(state -> StateDataType.EXTRACTED.equals(state)
                            || StateDataType.VALIDATED.equals(state));

            if (validatedState.isPresent()) {
                return JsonNodeFactory.instance.objectNode();
            }
            // if no state data or if state not extracted/validated
            return dataService.getData(surveyUnitId);
        }

        // reviewer cannot see data
        throw new AccessDeniedException("Not authorized to see survey unit data");
    }


    /**
     * Update the questionnaire form data of a survey unit
     *
     * @param dataValue    the questionnaire form data to update
     * @param surveyUnitId the id of the survey unit
     */
    @Operation(summary = "Update data for a survey unit",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    schema = @Schema(ref = SchemaType.Names.DATA))))
    @PutMapping(path = "/survey-unit/{id}/data")
    @PreAuthorize(AuthorityPrivileges.HAS_SURVEY_UNIT_PRIVILEGES)
    public void updateData(
            @NotNull
            @RequestBody
            @JsonValid(SchemaType.DATA)
            ObjectNode dataValue,
            @IdValid
            @PathVariable(value = "id")
            String surveyUnitId) throws ConflictException {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);

        SurveyUnitSummary surveyUnitSummary = surveyUnitService.getSummaryById(surveyUnitId);

        // if campaign sensitivity is OFF, update data
        if(surveyUnitSummary.campaign().getSensitivity().equals(CampaignSensitivity.NORMAL)) {
            dataService.saveData(surveyUnitId, dataValue);
            return;
        }

        // here, campaign sensitivity is ON !

        // admin can do everything
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.ADMIN, AuthorityRoleEnum.WEBCLIENT)){
            dataService.saveData(surveyUnitId, dataValue);
            return;
        }

        // interviewer/survey-unit can update data if survey is not ended
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.SURVEY_UNIT)){
            Optional<StateDataType> validatedState = stateDataService
                    .findStateData(surveyUnitId)
                    .map(StateData::state)
                    .filter(state -> StateDataType.EXTRACTED.equals(state)
                            || StateDataType.VALIDATED.equals(state));

            if (validatedState.isEmpty()) {
                dataService.saveData(surveyUnitId, dataValue);
                return;
            }

            throw new ConflictException(surveyUnitId);
        }
        throw new AccessDeniedException("Not authorized to update survey unit data");
    }
}
