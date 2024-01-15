package fr.insee.queen.infrastructure.depositproof;

import fr.insee.queen.domain.depositproof.gateway.DepositProofGeneration;
import fr.insee.queen.infrastructure.depositproof.exception.DepositProofException;
import fr.insee.queen.infrastructure.depositproof.generation.FOGeneration;
import fr.insee.queen.infrastructure.depositproof.generation.FoToPDF;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@RequiredArgsConstructor
@Slf4j
public class PDFDepositProofGeneration implements DepositProofGeneration {

    private final FOGeneration foGenerationComponent;
    private final FoToPDF foToPDFComponent;

    @Override
    public File generateDepositProof(String date, String campaignLabel, String userId) {
        File pdfFile;
        try {
            File foFile = foGenerationComponent.generateFo(date, campaignLabel, userId);
            pdfFile = foToPDFComponent.transformFoToPdf(foFile);
            Files.delete(foFile.toPath());
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new DepositProofException();
        }
        return pdfFile;
    }
}
