package fr.insee.queen.api.pdfutils;

import java.io.File;

public interface FormAndDataService {

    public abstract File getSurveyUnitData(String ...args) throws Exception;

    public abstract File getForm(String ...args) throws Exception;


}
