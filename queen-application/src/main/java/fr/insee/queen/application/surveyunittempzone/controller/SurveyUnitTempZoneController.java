package fr.insee.queen.application.surveyunittempzone.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.surveyunittempzone.dto.output.SurveyUnitTempZoneDto;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.surveyunittempzone.service.SurveyUnitTempZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle temp zone for survey units. The temp zone is used when interviewers synchronized orphan survey units
 */
@RestController
@Tag(name = "08. Survey units in temp Zone", description = "Endpoints for survey units in temporary zone")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class SurveyUnitTempZoneController {
    private final SurveyUnitTempZoneService surveyUnitTempZoneService;

    /**
     * Create a survey unit to the temp zone area
     *
     * @param surveyUnitId survey unit id
     * @param surveyUnit   survey unit json
     */
    @Operation(summary = "Create survey-unit to temp-zone")
    @Parameter(name = "userId", hidden = true)
    @PostMapping(path = "/survey-unit/{id}/temp-zone")
    @PreAuthorize(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void postSurveyUnitByIdInTempZone(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                             @NotNull @RequestBody ObjectNode surveyUnit,
                                             @CurrentSecurityContext(expression = "authentication.name") String userId) {
        surveyUnitTempZoneService.saveSurveyUnitToTempZone(surveyUnitId, userId, surveyUnit.toString());
    }

    /**
     * Retrieve all survey units in temp zone
     *
     * @return List of {@link SurveyUnitTempZoneDto} survey units
     */
    @Operation(summary = "GET all survey-units in temp-zone")
    @GetMapping(path = "/survey-units/temp-zone")
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
    public List<SurveyUnitTempZoneDto> getSurveyUnitsInTempZone() {
        return surveyUnitTempZoneService.getAllSurveyUnitTempZone()
                .stream().map(SurveyUnitTempZoneDto::fromModel)
                .toList();
    }
}
