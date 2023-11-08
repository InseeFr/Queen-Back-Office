package fr.insee.queen.api.controller.integration.dummy;

import fr.insee.queen.api.controller.integration.component.SchemaComponent;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class SchemaFakeComponent implements SchemaComponent {
    @Override
    public void throwExceptionIfXmlDataFileNotValid(ZipFile zf, String xmlFileName, String xsdSchemaFileName) throws IntegrationValidationException {

    }

    @Override
    public Document buildDocument(InputStream xmlFileStream) throws ParserConfigurationException, IOException, SAXException {
        return null;
    }
}
