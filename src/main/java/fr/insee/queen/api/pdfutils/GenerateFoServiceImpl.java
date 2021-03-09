package fr.insee.queen.api.pdfutils;

import java.io.*;

import javax.xml.transform.TransformerException;

public class GenerateFoServiceImpl implements GenerateFoService {

    private XslTransformation transformationService = new XslTransformation();

    @Override
    public File generateFo(File form, File surveyUnitData, String idec) throws Exception {
        File temp = mergeFormAndData(form, surveyUnitData);
        File fo = createFoFromFormWithData(temp, idec);
        temp.delete();
        return fo;
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

    public File createFoFromFormWithData(File formData, String idec) throws Exception{
        File outputFile = File.createTempFile("fo-file",".fo");

        InputStream inputStream = new FileInputStream(formData);
        OutputStream outputStream = new FileOutputStream(outputFile);
        InputStream XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATION_XSL_COMMON);

        try {
            transformationService.xslGenerateFo(inputStream, XSL, outputStream, idec);
        }catch(Exception e) {
            throw new Exception(e.getMessage());
        }
        inputStream.close();
        outputStream.close();
        XSL.close();

        return outputFile;
    }
}
