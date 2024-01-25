package fr.insee.queen.application.surveyunit.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * These endpoints handle the comment filled by a survey unit at the nd of the questionnaire*
 */

@ConditionalOnExpression(value = "${feature.comments.enabled} == true")
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;
    private final PilotageComponent pilotageComponent;

    /**
     * Retrieve the comment linked to the survey unit
     *
     * @param surveyUnitId the id of survey unit
     * @return {@link String} the comment linked to the survey unit
     */
    @Operation(summary = "Get comment for a survey unit")
    @GetMapping(path = "/survey-unit/{id}/comment")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public String getCommentBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        return commentService.getComment(surveyUnitId);
    }

    /**
     * Update the comment linked to the survey unit
     *
     * @param commentValue the value to update
     * @param surveyUnitId the id of the survey unit
     */
    @Operation(summary = "Update comment for a survey unit")
    @PutMapping(path = "/survey-unit/{id}/comment")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public void setComment(@NotNull @RequestBody ObjectNode commentValue,
                           @IdValid @PathVariable(value = "id") String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        commentService.updateComment(surveyUnitId, commentValue);
    }
}
