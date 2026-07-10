package fr.insee.queen.application.group.controller;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.group.service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handle the group metadata
 */
@RestController
@Tag(name = "05. Metadata", description = "Endpoints for retrieving metadata")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class MetadataController {

    private final MetadataService metadataService;

    /**
     * Retrieve metadata linked to a group
     *
     * @param groupId the id of the group
     * @return the metadata linked to the group
     */
    @Operation(summary = "Get metadata for a group ")
    @GetMapping(path = "/${application.group.path-singular}/{id}/metadata")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(ref = SchemaType.Names.METADATA))})
    public ObjectNode getMetadataByGroupId(@IdValid @PathVariable(value = "id") String groupId) {
        return metadataService.getMetadata(groupId);
    }
}
