package fr.insee.queen.api.pdfutils;

import java.io.File;
import java.nio.file.Files;


public class PDFDepositProofService {

    private final GenerateFoService generateFoService = new GenerateFoServiceImpl();
    private final FoToPDFTransformation foToPDFTransformation = new FoToPDFTransformation();


    public File generatePdf(String date, String campaignLabel, String idec) throws Exception {
        File foFile = generateFoService.generateFo(date, campaignLabel, idec);
        File pdfFile = foToPDFTransformation.transformFoToPdf(foFile);
        Files.delete(foFile.toPath());
        return pdfFile;
    }
}
