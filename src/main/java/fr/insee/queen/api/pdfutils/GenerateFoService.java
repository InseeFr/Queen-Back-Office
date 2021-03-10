package fr.insee.queen.api.pdfutils;

import java.io.File;

public interface GenerateFoService {

    public abstract File generateFo(String date, String campaignLabel, String idec) throws Exception;
}
