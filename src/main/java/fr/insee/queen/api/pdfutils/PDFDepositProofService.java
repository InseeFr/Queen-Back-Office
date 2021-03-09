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

    public File generatePdf(String surveyUnit, String survey, String surveyModel, String idec) throws Exception {
        File form = formAndDataService.getForm(survey,surveyModel);
        File data = formAndDataService.getSurveyUnitData(surveyUnit, survey, surveyModel);
        File foFile = generateFoService.generateFo(form,data,idec);
        File pdfFile = foToPDFTransformation.transformFoToPdf(foFile);
        form.delete(); data.delete(); foFile.delete();
        return pdfFile;
    }
}
