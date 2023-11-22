package fr.insee.queen.api.integration.controller;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.configuration.swagger.role.DisplayRolesOnUI;
import fr.insee.queen.api.integration.controller.component.IntegrationComponent;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultsDto;
import fr.insee.queen.api.web.authentication.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handle full integration of a campaign (campaign/questionnaires/nomenclatures)
 */
@RestController
@Tag(name = "01. Integrations", description = "Endpoints for integration")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class IntegrationController {
    private final AuthenticationHelper authHelper;
    private final IntegrationComponent integrationComponent;

    /**
     * Integrate a full campaign (campaign/nomenclatures/questionnaires
     * The results of the integration indicate the successful/failed integrations
     * (the campaign integration can be successful nut not the questionnaire integration for example)
     *
     * @param file the integration zip file containing all infos about campaign/questionnaire/nomenclatures
     * @return {@link IntegrationResultsDto} the results of the integration
     */
    @Operation(summary = "Integrates the context of a campaign")
    @PostMapping(path = "/campaign/context", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DisplayRolesOnUI
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    public IntegrationResultsDto integrateContext(@RequestParam("file") MultipartFile file) {
        String userId = authHelper.getUserId();
        log.info("User {} requests campaign creation via context ", userId);
        return integrationComponent.integrateContext(file);
    }
}
