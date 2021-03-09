package fr.insee.queen.api.pdfutils;

import java.io.File;

public interface GenerateFoService {

    public abstract File generateFo(File form, File surveyUnitData, String idec) throws Exception;
}
