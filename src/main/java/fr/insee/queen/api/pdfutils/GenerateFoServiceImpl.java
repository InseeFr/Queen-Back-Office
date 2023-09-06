package fr.insee.queen.api.pdfutils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class GenerateFoServiceImpl implements GenerateFoService {

    private final XslTransformation transformationService = new XslTransformation();

    @Override
    public File generateFo(String date, String campaignLabel, String idec) throws Exception {
        return createFoFromFormWithData(date, campaignLabel, idec);
    }

    public File mergeFormAndData(File form, File surveyUnitData) throws Exception{
        File outputFile = File.createTempFile("fo-file",".fo");

        InputStream formIS = new FileInputStream(form);
        InputStream dataIS = new FileInputStream(surveyUnitData);
        OutputStream outputStream = new FileOutputStream(outputFile);
        InputStream xsl = Constants.getInputStreamFromPath(Constants.TRANSFORMATION_XSL_MERGE_DATA_FORM);

        try {
            transformationService.xslMergeFormAndData(formIS, dataIS, xsl, outputStream);
        }catch(Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            formIS.close();
            dataIS.close();
            outputStream.close();
            if(xsl != null) {
                xsl.close();
            }
        }


        return outputFile;
    }

    public File createFoFromFormWithData(String date,
    									String campaignLabel,
    									String idec) throws Exception{

        File outputFile = File.createTempFile("fo-file",".fo");
        log.info(outputFile.getAbsolutePath());
        InputStream inputStream =  Constants.getEmptyXml();
        OutputStream outputStream = new FileOutputStream(outputFile);
        InputStream xsl = Constants.getInputStreamFromPath(Constants.TRANSFORMATION_XSL_COMMON);

        try {
            transformationService.xslGenerateFo(inputStream, xsl, outputStream, date, campaignLabel, idec);
        }catch(Exception e) {
            throw new Exception(e.getMessage());
        }
        inputStream.close();
        outputStream.close();
        xsl.close();

        return outputFile;
    }
}
