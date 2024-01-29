package fr.insee.queen.application.campaign.controller;

import fr.insee.queen.application.campaign.dto.input.QuestionnaireModelCreationData;
import fr.insee.queen.application.campaign.dto.output.QuestionnaireModelIdDto;
import fr.insee.queen.application.campaign.dto.output.QuestionnaireModelValueDto;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitDto;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitOkNokDto;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.service.QuestionnaireModelService;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
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
     * Retrieve the data structure of all questionnaires linked to a campaign
     *
     * @param campaignId the id of campaign
     * @return List of {@link QuestionnaireModelValueDto} linked to the campaign
     */
    @Operation(summary = "Get questionnaire list for a campaign ")
    @GetMapping(path = "/campaign/{id}/questionnaires")
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
    public List<QuestionnaireModelValueDto> getQuestionnaireDatasByCampaignId(
            @IdValid @PathVariable(value = "id") String campaignId) {
        return questionnaireModelService
                .getQuestionnaireDatas(campaignId).stream()
                .map(QuestionnaireModelValueDto::new)
                .toList();
    }

    /**
     * Retrieve the data structure of a questionnaire
     *
     * @param questionnaireModelId the id of questionnaire
     * @return the {@link QuestionnaireModelValueDto} linked to the id
     */
    @Operation(summary = "Get questionnnaire")
    @GetMapping(path = "/questionnaire/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public QuestionnaireModelValueDto getQuestionnaireData(@IdValid @PathVariable(value = "id") String questionnaireModelId) {
        return new QuestionnaireModelValueDto(questionnaireModelService.getQuestionnaireData(questionnaireModelId));
    }

    /**
     * Retrieve all the questionnaire ids for a campaign
     *
     * @param campaignId the campaign id
     * @return List of {@link QuestionnaireModelIdDto} list linked to the campaign id
     */
    @Operation(summary = "Get list of questionnaire ids for a campaign")
    @GetMapping(path = "/campaign/{id}/questionnaire-id")
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
    public List<QuestionnaireModelIdDto> getQuestionnaireIdsByCampaignId(
            @IdValid @PathVariable(value = "id") String campaignId) {
        return questionnaireModelService
                .getQuestionnaireIds(campaignId)
                .stream()
                .map(QuestionnaireModelIdDto::new)
                .toList();
    }

    /**
     * Create a questionnaire model
     *
     * @param questionnaireModelData questionnaire data used to create a questionnaire
     */
    @Operation(summary = "Create a Questionnaire Model")
    @PostMapping(path = "/questionnaire-models")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void createQuestionnaire(@RequestBody @Valid QuestionnaireModelCreationData questionnaireModelData) {
        questionnaireModelService.createQuestionnaire(QuestionnaireModelCreationData.toModel(questionnaireModelData));
    }

    /**
     * Search questionnaire ids linked to survey units
     *
     * @param surveyUnitIdsToSearch survey unit ids where we want to retrive the questionnaire ids
     * @return {@link SurveyUnitOkNokDto} list of survey units with their questionnaire ids, and list of survey units where no questionnaire found
     */
    @Operation(summary = "Search questionnaire ids linked to survey units")
    @PostMapping(path = "/survey-units/questionnaire-model-id")
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
    public ResponseEntity<SurveyUnitOkNokDto> getQuestionnaireModelIdBySurveyUnits(
            @NotEmpty @RequestBody List<String> surveyUnitIdsToSearch) {
        List<SurveyUnitSummary> surveyUnitsFound = surveyUnitService.findSummariesByIds(surveyUnitIdsToSearch);
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
