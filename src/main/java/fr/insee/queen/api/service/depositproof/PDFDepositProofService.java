package fr.insee.queen.api.service.depositproof;

import fr.insee.queen.api.exception.DepositProofException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@AllArgsConstructor
@Slf4j
public class PDFDepositProofService {

    private final GenerateFoService generateFoService;
    private final FoToPDFService foToPDFService;

    public File retrievePdf(String date, String campaignLabel, String userId) {
        File pdfFile;
        try {
            File foFile = generateFoService.generateFo(date, campaignLabel, userId);
            pdfFile = foToPDFService.transformFoToPdf(foFile);
            Files.delete(foFile.toPath());
        } catch(IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new DepositProofException();
        }
        return pdfFile;
    }
}
