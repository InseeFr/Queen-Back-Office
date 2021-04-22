package fr.insee.queen.api.pdfutils;

import java.io.File;


public class PDFDepositProofService {

    private GenerateFoService generateFoService = new GenerateFoServiceImpl();
    private FoToPDFTransformation foToPDFTransformation = new FoToPDFTransformation();


    public File generatePdf(String date, String campaignLabel, String idec) throws Exception {
        File foFile = generateFoService.generateFo(date, campaignLabel, idec);
        File pdfFile = foToPDFTransformation.transformFoToPdf(foFile);
        foFile.delete();
        return pdfFile;
    }
}
