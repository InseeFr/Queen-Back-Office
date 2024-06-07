package fr.insee.queen.application.depositproof.controller;

import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.depositproof.model.PdfDepositProof;
import fr.insee.queen.domain.depositproof.service.DepositProofService;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;

/**
 * Handle survey units
 */
@RestController
@Tag(name = "06. Survey units", description = "Endpoints for survey units")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class DepositProofController {
    private final DepositProofService depositProofService;
    private final PilotageComponent pilotageComponent;

    /**
     * Generate and retrieve a deposit proof (pdf file) for a survey unit
     *
     * @param surveyUnitId survey unit id
     */
    @Operation(summary = "Get deposit proof for a survey unit")
    @Parameter(name = "userId", hidden = true)
    @GetMapping(value = "/survey-unit/{id}/deposit-proof")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public ResponseEntity<FileSystemResource> generateDepositProof(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                                                   @CurrentSecurityContext(expression = "authentication.name")
                                     String userId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);

        PdfDepositProof depositProof = depositProofService.generateDepositProof(userId, surveyUnitId);
        File pdfFile = depositProof.depositProof();
        long fileLength = pdfFile.length();

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_PDF);
        respHeaders.setContentLength(fileLength);
        respHeaders.setContentDispositionFormData("attachment", depositProof.filename());

        FileSystemResource pdfResource = new FileSystemResource(pdfFile) {
            @Override
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(pdfFile) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        Files.delete(pdfFile.toPath());
                    }
                };
            }
        };

        return new ResponseEntity<>(
                pdfResource, respHeaders, HttpStatus.OK
        );
    }
}
