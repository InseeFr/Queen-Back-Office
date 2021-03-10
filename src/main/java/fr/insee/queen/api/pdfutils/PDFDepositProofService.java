package fr.insee.queen.api.pdfutils;

import java.io.File;

import fr.insee.queen.api.pdfutils.GenerateFoServiceImpl;

public class PDFDepositProofService {

    private FormAndDataService formAndDataService;
    private GenerateFoService generateFoService = new GenerateFoServiceImpl();
    private FoToPDFTransformation foToPDFTransformation = new FoToPDFTransformation();

    public PDFDepositProofService(String urlApi){
        this.formAndDataService = new FormAndDataServiceImpl(urlApi);
    }

    public File generatePdf(String date, String campaignLabel, String idec) throws Exception {
        File foFile = generateFoService.generateFo(date, campaignLabel, idec);
        File pdfFile = foToPDFTransformation.transformFoToPdf(foFile);
        foFile.delete();
        return pdfFile;
    }
}
