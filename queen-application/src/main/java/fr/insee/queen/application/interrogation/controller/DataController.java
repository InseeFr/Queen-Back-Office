package fr.insee.queen.application.interrogation.controller;

import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.application.web.validation.json.JsonValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.service.DataService;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * These endpoints handle the questionnaire form data of an interrogation
 */
@RestController
@Tag(name = "06. Interrogations")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class DataController {
    private final DataService dataService;
    private final PilotageComponent pilotageComponent;
    private final StateDataService stateDataService;
    private final InterrogationService interrogationService;
    private final AuthenticationHelper authenticationUserHelper;

    /**
     * Retrieve the questionnaire form data of an interrogation
     *
     * @param interrogationId the id of reporting unit
     * @return {@link String} the questionnaire form data of an interrogation
     */
    @Operation(summary = "Get data for an interrogation")
    @GetMapping("/interrogations/{id}/data")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(ref = SchemaType.Names.DATA))})
    public ObjectNode getDataByInterrogation(@IdValid @PathVariable(value = "id") String interrogationId) {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);
        return dataService.getData(interrogationId);
    }


    /**
     * Update the questionnaire form data of an interrogation
     *
     * @param dataValue    the questionnaire form data to update
     * @param interrogationId the id of the interrogation
     */
    @Operation(summary = "Update data for an interrogation",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    schema = @Schema(ref = SchemaType.Names.DATA))))
    @PutMapping("/interrogations/{id}/data")
    @PreAuthorize(AuthorityPrivileges.HAS_SURVEY_UNIT_PRIVILEGES)
    public void updateData(
            @NotNull
            @RequestBody
            @JsonValid(SchemaType.DATA)
            ObjectNode dataValue,
            @IdValid
            @PathVariable(value = "id")
            String interrogationId) {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);
        dataService.saveData(interrogationId, dataValue);
    }

    /**
     * Clean all data from interrogations of a campaign with extracted state
     *
     * @param campaignId the campaign id
     */
    @Operation(summary = "Clean all data from interrogations of a campaign with extracted state")
    @DeleteMapping("/admin/campaign/{id}/interrogations/data/extracted")
    @PreAuthorize("hasRole('WEBCLIENT')")
    public void cleanData(@IdValid @PathVariable(value = "id") String campaignId,
                          @NotNull @RequestParam("start") Long startTimestamp,
                          @NotNull @RequestParam("end") Long endTimestamp) {
        dataService.cleanExtractedData(campaignId, startTimestamp, endTimestamp);
    }

    /**
     * Clean data from extracted interrogations of a campaign, restricted to the provided ids
     *
     * @param campaignId        the campaign id
     * @param interrogationIds  the interrogation ids to clean
     */
    @Operation(summary = "Clean data from extracted interrogations of a campaign, filtered by ids")
    @PostMapping("/admin/campaign/{id}/interrogations/data/extracted/clean")
    @PreAuthorize("hasRole('WEBCLIENT')")
    public void cleanExtractedDataByIds(
            @IdValid @PathVariable(value = "id") String campaignId,
            @NotEmpty @RequestBody List<String> interrogationIds) {
        dataService.cleanExtractedDataByIds(campaignId, interrogationIds);
    }
}
