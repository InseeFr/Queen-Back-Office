package fr.insee.queen.api.pdfutils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;



public class FormAndDataServiceImpl implements FormAndDataService {

    public File getXmlFromDataBase(String requestUrl) throws IOException {
        File xmlFile = File.createTempFile("xml-data",".xml");
        InputStream fakeData = Constants.getEmptyXml();
        Files.copy(fakeData, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        if(fakeData != null) fakeData.close();
        

        return xmlFile;
    }
}
