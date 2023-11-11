package fr.insee.queen.api.controller.integration.component;

import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;


public interface SchemaComponent {
    void throwExceptionIfXmlDataFileNotValid(ZipFile zipFile, String xmlFileName, String xsdSchemaFileName) throws IntegrationValidationException;
    Document buildDocument(InputStream xmlFileStream) throws ParserConfigurationException, IOException, SAXException;
}
