package fr.insee.queen.application.group.controller;

import fr.insee.queen.application.group.dto.input.GroupCreationRequest;
import fr.insee.queen.application.group.dto.input.GroupCreationRequestV2;
import fr.insee.queen.application.group.dto.output.GroupResponse;
import fr.insee.queen.application.group.dto.output.GroupSummaryResponse;
import fr.insee.queen.application.group.dto.output.GroupIdsResponse;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle groups
 */
@RestController
@Tag(name = "02. Groups", description = "Endpoints for groups")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class GroupController {
    private final GroupService groupService;

    /**
     * Retrieve all groups
     *
     * @return List of all {@link GroupSummaryResponse}
     */
    @Operation(summary = "Get list of all groups")
    @GetMapping(path = "/admin/${application.group.path-plural}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public List<GroupSummaryResponse> getGroups() {
        return groupService.getAllGroups()
                .stream().map(GroupSummaryResponse::fromModel)
                .toList();
    }

    /**
     * Retrieve all group ids
     *
     * @return List of all {@link GroupIdsResponse}
     */
    @Operation(summary = "Get list of all group ids")
    @GetMapping(path = "/${application.group.path-plural}/ids")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public List<GroupIdsResponse> getGroupsIds() {
        return groupService.getAllGroupIds()
                .stream().map(GroupIdsResponse::fromModel)
                .toList();
    }

    /**
     * Retrieve a group
     *
     * @return {@link GroupSummaryResponse}
     */
    @Operation(summary = "Get group")
    @GetMapping(path = "/admin/${application.group.path-plural}/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public GroupResponse getGroup(@IdValid @PathVariable(value = "id") String groupId) {
        return GroupResponse.fromModel(groupService.getGroup(groupId));
    }

    /**
     * @deprecated
     * Create a new group
     *
     * @param groupRequest the value to create
     */
    @Deprecated(since = "4.3.0")
    @Operation(summary = "Create a group")
    @PostMapping(path = "/${application.group.path-plural}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void createGroup(@Valid @RequestBody GroupCreationRequest groupRequest) {
        groupService.createGroup(GroupCreationRequest.toModel(groupRequest));
    }

    /**
     * Create a new group
     *
     * @param groupRequest the value to create
     */
    @Operation(summary = "Create a group")
    @PostMapping(path = "/${application.group.path-singular}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void createGroupV2(@Valid @RequestBody GroupCreationRequestV2 groupRequest) {
        groupService.createGroup(GroupCreationRequestV2.toModel(groupRequest));
    }
}
