package fr.insee.queen.api.pdfutils;

import java.io.*;


public class GenerateFoServiceImpl implements GenerateFoService {

    private XslTransformation transformationService = new XslTransformation();

    @Override
    public File generateFo(String date, String campaignLabel, String idec) throws Exception {
        return createFoFromFormWithData(date, campaignLabel, idec);
    }

    public File mergeFormAndData(File form, File surveyUnitData) throws Exception{
        File outputFile = File.createTempFile("fo-file",".fo");

        InputStream formIS = new FileInputStream(form);
        InputStream dataIS = new FileInputStream(surveyUnitData);
        OutputStream outputStream = new FileOutputStream(outputFile);
        InputStream XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATION_XSL_MERGE_DATA_FORM);

        try {
            transformationService.xslMergeFormAndData(formIS, dataIS, XSL, outputStream);
        }catch(Exception e) {
            throw new Exception(e.getMessage());
        }
        formIS.close();
        dataIS.close();
        outputStream.close();
        XSL.close();

        return outputFile;
    }

    public File createFoFromFormWithData(String date,
    									String campaignLabel,
    									String idec) throws Exception{
        File outputFile = File.createTempFile("fo-file",".fo");
        InputStream inputStream =  Constants.getEmptyXml();
        OutputStream outputStream = new FileOutputStream(outputFile);
        InputStream XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATION_XSL_COMMON);

        try {
            transformationService.xslGenerateFo(inputStream, XSL, outputStream, date, campaignLabel, idec);
        }catch(Exception e) {
            throw new Exception(e.getMessage());
        }
        inputStream.close();
        outputStream.close();
        XSL.close();

        return outputFile;
    }
}
