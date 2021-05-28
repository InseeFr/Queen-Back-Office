package fr.insee.queen.api.pdfutils;

import java.io.File;
import java.io.IOException;

public interface FormAndDataService {

	 File getXmlFromDataBase(String requestUrl) throws IOException;

}
