package fr.insee.queen.api.integration.controller;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.integration.controller.component.IntegrationComponent;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultsDto;
import fr.insee.queen.api.web.authentication.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
     * Integrate a full campaign
     *
     * @param file the integration zip file containing all infos about campaign/questionnaire/nomenclatures
     * @param auth authentication object
     * @return {@link IntegrationResultsDto} the result of the integration
     */
    @Operation(summary = "Integrates the context of a campaign")
    @PostMapping(path = "/campaign/context", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    public IntegrationResultsDto integrateContext(@RequestParam("file") MultipartFile file,
                                                  Authentication auth) {
        String userId = authHelper.getUserId(auth);
        log.info("User {} requests campaign creation via context ", userId);
        return integrationComponent.integrateContext(file);
    }
}