package fr.insee.queen.api.controller.surveyunit;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.controller.utils.AuthenticationHelper;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitTempZoneDto;
import fr.insee.queen.api.service.surveyunit.SurveyUnitService;
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
 * SurveyUnitController is the Controller using to manage survey units
 *
 * @author Claudel Benjamin
 *
 */
@RestController
@Tag(name = "08. Survey units in temp Zone", description = "Endpoints for survey units in temporary zone")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class SurveyUnitTempZoneController {
    /**
     * The survey unit repository using to access to table 'survey_unit' in DB
     */
    private final SurveyUnitService surveyUnitService;
    private final AuthenticationHelper authHelper;

    /**
     * This method is used to post a survey-unit by id to a temp-zone
     */
    @Operation(summary = "Create survey-unit to temp-zone")
    @PostMapping(path = "/survey-unit/{id}/temp-zone")
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES + "||" + AuthorityRole.INTERVIEWER)
    @ResponseStatus(HttpStatus.CREATED)
    public void postSurveyUnitByIdInTempZone(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                             @NotNull @RequestBody ObjectNode surveyUnit,
                                             Authentication auth) {
        log.info("POST survey-unit to temp-zone");
        String userId = authHelper.getUserId(auth);
        surveyUnitService.saveSurveyUnitToTempZone(surveyUnitId, userId, surveyUnit);
    }

    /**
     * This method is used to retrieve survey-units in temp-zone
     */
    @Operation(summary = "GET all survey-units in temp-zone")
    @GetMapping(path = "/survey-units/temp-zone")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public List<SurveyUnitTempZoneDto> getSurveyUnitsInTempZone() {
        log.info("GET all survey-units in temp-zone");
        return surveyUnitService.getAllSurveyUnitTempZoneDto();
    }
}
