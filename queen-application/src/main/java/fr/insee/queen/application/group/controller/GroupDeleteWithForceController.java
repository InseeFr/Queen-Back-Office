package fr.insee.queen.application.group.controller;

import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.group.service.GroupService;
import fr.insee.queen.domain.group.service.exception.GroupDeletionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Handle groups deletions, with full deletion (used in dev/test environments)
 */
@RestController
@Tag(name = "02. Groups", description = "Endpoints for groups")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
@ConditionalOnProperty(name = "application.group.check-interrogations-on-delete", havingValue="false")
public class GroupDeleteWithForceController {

    private final GroupService groupService;
    private final PilotageComponent pilotageComponent;

    /**
     * Delete a group. The deletion is processed in two cases:
     * - the group is closed (check on pilotage api)
     * - pilotage api is disabled or force option is set to true
     *
     * @param force      force the full deletion of the group (without checking if group is closed in pilotage api)
     * @param groupId group id
     */
    @Operation(summary = "Delete a group")
    @DeleteMapping(path = "/${application.group.path-singular}/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGroupById(@RequestParam("force") boolean force,
                                   @IdValid @PathVariable(value = "id") String groupId) {
        if (force || pilotageComponent.isClosed(groupId)) {
            groupService.delete(groupId, true);
            log.info("Group with id {} deleted", groupId);
            return;
        }

        throw new GroupDeletionException(String.format("Unable to delete group %s, group isn't closed", groupId));
    }
}
