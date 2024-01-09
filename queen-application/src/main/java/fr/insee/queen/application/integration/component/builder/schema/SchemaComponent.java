package fr.insee.queen.application.integration.component.builder.schema;

import fr.insee.queen.application.integration.component.exception.IntegrationValidationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;


public interface SchemaComponent {
    /**
     * Check if the xml file in zip is valid
     *
     * @param zipFile the zip file
     * @param xmlFileName the name of the xml file to check
     * @param xsdSchemaFileName xsd schema used to check the xml file
     * @throws IntegrationValidationException integration validation exception
     */
    void throwExceptionIfXmlDataFileNotValid(ZipFile zipFile, String xmlFileName, String xsdSchemaFileName) throws IntegrationValidationException;

    /**
     * Check if the file exists in zip
     *
     * @param zipFile the zip file
     * @param fileName the name of the xml file to check
     * @throws IntegrationValidationException integration validation exception
     */
    void throwExceptionIfDataFileNotExist(ZipFile zipFile, String fileName) throws IntegrationValidationException;

    /**
     * Build a document from an xml input stream
     *
     * @param xmlFileStream xml file input stream
     * @return the document built from an xml input stream
     * @throws ParserConfigurationException parsing exception
     * @throws IOException io exception
     * @throws SAXException sax exception
     */
    Document buildDocument(InputStream xmlFileStream) throws ParserConfigurationException, IOException, SAXException;
}
