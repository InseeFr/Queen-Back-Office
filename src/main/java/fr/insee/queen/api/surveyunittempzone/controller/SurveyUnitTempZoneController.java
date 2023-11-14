package fr.insee.queen.api.surveyunittempzone.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.surveyunittempzone.controller.dto.output.SurveyUnitTempZoneDto;
import fr.insee.queen.api.surveyunittempzone.service.SurveyUnitTempZoneService;
import fr.insee.queen.api.web.authentication.AuthenticationHelper;
import fr.insee.queen.api.web.validation.IdValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationHelper authHelper;

    /**
     * Create a survey unit to the temp zone area
     *
     * @param surveyUnitId survey unit id
     * @param surveyUnit   survey unit json
     * @param auth         authenticated user
     */
    @Operation(summary = "Create survey-unit to temp-zone")
    @PostMapping(path = "/survey-unit/{id}/temp-zone")
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES + "||" + AuthorityRole.HAS_ROLE_INTERVIEWER)
    @ResponseStatus(HttpStatus.CREATED)
    public void postSurveyUnitByIdInTempZone(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                             @NotNull @RequestBody ObjectNode surveyUnit,
                                             Authentication auth) {
        log.info("POST survey-unit to temp-zone");
        String userId = authHelper.getUserId(auth);
        surveyUnitTempZoneService.saveSurveyUnitToTempZone(surveyUnitId, userId, surveyUnit);
    }

    /**
     * Retrieve all survey units in temp zone
     *
     * @return List of {@link SurveyUnitTempZoneDto} survey units
     */
    @Operation(summary = "GET all survey-units in temp-zone")
    @GetMapping(path = "/survey-units/temp-zone")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public List<SurveyUnitTempZoneDto> getSurveyUnitsInTempZone() {
        log.info("GET all survey-units in temp-zone");
        return surveyUnitTempZoneService.getAllSurveyUnitTempZone()
                .stream().map(SurveyUnitTempZoneDto::fromModel)
                .toList();
    }
}
