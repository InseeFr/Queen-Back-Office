package fr.insee.queen.api.campaign.controller;

import fr.insee.queen.api.campaign.controller.dto.input.QuestionnaireModelCreationData;
import fr.insee.queen.api.campaign.controller.dto.output.QuestionnaireModelIdDto;
import fr.insee.queen.api.campaign.controller.dto.output.QuestionnaireModelValueDto;
import fr.insee.queen.api.campaign.service.QuestionnaireModelService;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitDto;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitOkNokDto;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import fr.insee.queen.api.web.validation.IdValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle questionnaire models. A questionnaire model is the definition of a questionnaire with its data internal structure
 */
@RestController
@Tag(name = "03. Questionnaires", description = "Endpoints for questionnaires")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class QuestionnaireModelController {
    private final SurveyUnitService surveyUnitService;
    private final QuestionnaireModelService questionnaireModelService;

    /**
     * Retrieve the data structure of all questionnaires associated to a campaign
     *
     * @param campaignId the id of campaign
     * @return List of {@link QuestionnaireModelValueDto} associated to the campaign
     */
    @Operation(summary = "Get questionnaire list for a specific campaign ")
    @GetMapping(path = "/campaign/{id}/questionnaires")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public List<QuestionnaireModelValueDto> getQuestionnaireDatasByCampaignId(
            @IdValid @PathVariable(value = "id") String campaignId) {
        log.info("GET questionnaire for campaign with id {}", campaignId);
        return questionnaireModelService
                .getQuestionnaireDatas(campaignId).stream()
                .map(QuestionnaireModelValueDto::new)
                .toList();
    }

    /**
     * Retrieve the data structure of a questionnaire
     *
     * @param questionnaireModelId the id of questionnaire
     * @return the {@link QuestionnaireModelValueDto} associated to the id
     */
    @Operation(summary = "Get questionnnaire")
    @GetMapping(path = "/questionnaire/{id}")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public QuestionnaireModelValueDto getQuestionnaireData(@IdValid @PathVariable(value = "id") String questionnaireModelId) {
        log.info("GET questionnaire for id {}", questionnaireModelId);
        return new QuestionnaireModelValueDto(questionnaireModelService.getQuestionnaireData(questionnaireModelId));
    }

    /**
     * Retrieve all the questionnaire ids for a campaign
     *
     * @param campaignId the campaign id
     * @return List of {@link QuestionnaireModelIdDto} list associated to the campaign id
     */
    @Operation(summary = "Get list of questionnaire ids for a specific campaign")
    @GetMapping(path = "/campaign/{id}/questionnaire-id")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public List<QuestionnaireModelIdDto> getQuestionnaireIdsByCampaignId(
            @IdValid @PathVariable(value = "id") String campaignId) {
        log.info("GET questionnaire Id for campaign with id {}", campaignId);
        return questionnaireModelService
                .getQuestionnaireIds(campaignId)
                .stream()
                .map(QuestionnaireModelIdDto::new)
                .toList();
    }

    /**
     * Create a questionnaire model
     *
     * @param questionnaireModelInput questionnaire data used to create a questionnaire
     */
    @Operation(summary = "Create a Questionnaire Model")
    @PostMapping(path = "/questionnaire-models")
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void createQuestionnaire(@RequestBody @Valid QuestionnaireModelCreationData questionnaireModelInput) {
        log.info("POST Questionnaire Model with id {}", questionnaireModelInput.idQuestionnaireModel());
        questionnaireModelService.createQuestionnaire(QuestionnaireModelCreationData.toModel(questionnaireModelInput));
    }

    /**
     * Search questionnaire ids associated to survey units
     *
     * @param surveyUnitIdsToSearch survey unit ids where we want to retrive the questionnaire ids
     * @return {@link SurveyUnitOkNokDto} list of survey units with their questionnaire ids, and list of survey units where no questionnaire found
     */
    @Operation(summary = "Search questionnaire ids associated to survey units")
    @PostMapping(path = "/survey-units/questionnaire-model-id")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public ResponseEntity<SurveyUnitOkNokDto> getQuestionnaireModelIdBySurveyUnits(
            @NotEmpty @RequestBody List<String> surveyUnitIdsToSearch) {
        List<SurveyUnitSummary> surveyUnitsFound = surveyUnitService.findSummaryByIds(surveyUnitIdsToSearch);
        List<String> surveyUnitIdsFound = surveyUnitsFound.stream().map(SurveyUnitSummary::id).toList();
        List<SurveyUnitDto> surveyUnitsNOK = surveyUnitIdsToSearch.stream()
                .filter(surveyUnitIdToSearch -> !surveyUnitIdsFound.contains(surveyUnitIdToSearch))
                .map(SurveyUnitDto::createSurveyUnitNOKDto)
                .toList();
        List<SurveyUnitDto> surveyUnitsOK = surveyUnitsFound.stream()
                .map(su -> SurveyUnitDto.createSurveyUnitOKDtoWithQuestionnaireModel(su.id(), su.questionnaireId()))
                .toList();
        return new ResponseEntity<>(new SurveyUnitOkNokDto(surveyUnitsOK, surveyUnitsNOK), HttpStatus.OK);
    }
}
