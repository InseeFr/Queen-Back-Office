package fr.insee.queen.application.interrogation.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.service.CommentService;
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
 * These endpoints handle the comment filled by an interrogation at the nd of the questionnaire*
 */

@ConditionalOnExpression(value = "${feature.comments.enabled} == true")
@RestController
@Tag(name = "06. Interrogations")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;
    private final PilotageComponent pilotageComponent;

    /**
     * Retrieve the comment linked to the interrogation
     *
     * @param interrogationId the id of interrogation
     * @return {@link String} the comment linked to the interrogation
     */
    @Operation(summary = "Get comment for an interrogation")
    @GetMapping("/interrogations/{id}/comment")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public ObjectNode getCommentByInterrogation(@IdValid @PathVariable(value = "id") String interrogationId) {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);
        return commentService.getComment(interrogationId);
    }

    /**
     * Update the comment linked to the interrogation
     *
     * @param commentValue the value to update
     * @param interrogationId the id of the interrogation
     */
    @Operation(summary = "Update comment for an interrogation")
    @PutMapping("/interrogations/{id}/comment")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public void setComment(@NotNull @RequestBody ObjectNode commentValue,
                           @IdValid @PathVariable(value = "id") String interrogationId) {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);
        commentService.updateComment(interrogationId, commentValue);
    }
}
