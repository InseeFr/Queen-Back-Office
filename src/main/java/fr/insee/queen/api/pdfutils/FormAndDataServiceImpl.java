package fr.insee.queen.api.pdfutils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FormAndDataServiceImpl implements FormAndDataService {

    private static Logger LOGGER = LoggerFactory.getLogger(FormAndDataServiceImpl.class);
    private String URL_API;

    public FormAndDataServiceImpl(String urlApi){
        this.URL_API = urlApi;
    }

    @Override
    public File getSurveyUnitData(String... args) throws Exception {
        String surveyUnit = args[0];
        String survey = args[1];
        String surveyModel = args[2];
        String requestUrl = String.format(
                URL_API.concat("/restxq/collectes/form/%s/%s/%s"),
                survey, surveyModel, surveyUnit);
        return getXmlFromDataBase(requestUrl);
    }

    @Override
    public File getForm(String... args) throws Exception {
        String survey = args[0];
        String surveyModel = args[1];
        String requestUrl = "http://void";
        return getXmlFromDataBase(requestUrl);
    }

    private File getXmlFromDataBase(String requestUrl) throws IOException {
        File xmlFile = File.createTempFile("xml-data",".xml");
        URL url = new URL(requestUrl);
        InputStream fakeData = Constants.getEmptyXml();
        Files.copy(fakeData, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        if(fakeData != null) fakeData.close();
        

        return xmlFile;
    }
}
