package fr.insee.queen.application.integration.controller;

import fr.insee.queen.application.configuration.auth.AuthorityRole;
import fr.insee.queen.application.integration.component.IntegrationComponent;
import fr.insee.queen.application.integration.dto.output.IntegrationResultsDto;
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
    private final IntegrationComponent integrationComponent;

    /**
     * Integrate a full campaign (campaign/nomenclatures/questionnaires
     * The results of the integration indicate the successful/failed integrations
     * (the campaign integration can be successful nut not the questionnaire integration for example)
     *
     * @param file the integration zip file containing all infos about campaign/questionnaire/nomenclatures
     * @return {@link IntegrationResultsDto} the results of the integration
     */
    @Operation(summary = "Integrates the context of a campaign (JSON version)")
    @PostMapping(path = "/campaign/context", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    public IntegrationResultsDto integrateContext(@RequestParam("file") MultipartFile file) {
        return integrationComponent.integrateContext(file, false);
    }

    /**
     * @deprecated
     * Integrate a full campaign (campaign/nomenclatures/questionnaires
     * The results of the integration indicate the successful/failed integrations
     * (the campaign integration can be successful nut not the questionnaire integration for example)
     *
     * @param file the integration zip file containing all infos about campaign/questionnaire/nomenclatures
     * @return {@link IntegrationResultsDto} the results of the integration
     */
    @Operation(summary = "Integrates the context of a campaign (XML Version - will be removed in a future version)")
    @PostMapping(path = "/campaign/xml/context", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    @Deprecated(since = "4.0.0")
    public IntegrationResultsDto integrateXmlContext(@RequestParam("file") MultipartFile file) {
        return integrationComponent.integrateContext(file, true);
    }
}
