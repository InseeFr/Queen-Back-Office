package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.group.dto.output.GroupSummaryResponse;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.interrogation.dto.output.InterrogationByGroupResponse;
import fr.insee.queen.application.interrogation.dto.output.InterrogationDto;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.pilotage.model.PilotageGroup;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
@ConditionalOnProperty(name = "feature.interviewer-mode.enabled", havingValue="true")
public class InterviewerController {
    private final PilotageComponent pilotageComponent;

    /**
     * Retrieve the groups the current user has access to
     *
     * @return List of {@link GroupSummaryResponse}
     */
    @Operation(summary = "Get group list for the current user")
    @Parameter(name = "userId", hidden = true)
    @Tag(name = "02. Groups")
    @GetMapping(path = "/${application.group.path-plural}")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
    public List<GroupSummaryResponse> getInterviewerGroupList(@CurrentSecurityContext(expression = "authentication.name")
                                                                   String userId) {

        List<PilotageGroup> groups = pilotageComponent.getInterviewerGroups();
        log.info("{} group(s) found for {}", groups.size(), userId);

        return groups.stream()
                .map(GroupSummaryResponse::fromPilotageModel)
                .toList();
    }

    /**
     * Retrieve all the interrogations of the current interviewer
     *
     * @return List of {@link InterrogationDto} interrogations
     */
    @Operation(summary = "Get list of interrogations linked to the current interviewer")
    @Tag(name = "06. Interrogations")
    @GetMapping("/interrogations/interviewer")
    @PreAuthorize(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES)
    public List<InterrogationDto> getInterviewerInterrogations() {
        // get interrogations for the interviewer
        List<Interrogation> interrogations = pilotageComponent.getInterviewerInterrogations();

        return interrogations.stream()
                .map(InterrogationDto::fromModel)
                .toList();
    }

    /**
     * Retrieve all the interrogations of a group
     *
     * @param groupId the id of group
     * @return List of {@link InterrogationByGroupResponse}
     */
    @Operation(summary = "Get list of interrogations for a group")
    @Tag(name = "06. Interrogations")
    @GetMapping("/${application.group.path-singular}/{id}/interrogations")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
    public List<InterrogationByGroupResponse> getListInterrogationByGroup(@IdValid @PathVariable(value = "id") String groupId) {
        // get interrogations of a group from the pilotage api
        List<InterrogationSummary> interrogations = pilotageComponent.getInterrogations(groupId);

        return interrogations.stream()
                .map(InterrogationByGroupResponse::fromModel)
                .toList();
    }
}
