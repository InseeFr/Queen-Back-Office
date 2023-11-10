package fr.insee.queen.api.controller.integration.component;

import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


@Component
public class SchemaIntegrationComponent implements SchemaComponent {

    @Override
    public void throwExceptionIfXmlDataFileNotValid(ZipFile zipFile, String xmlFileName, String xsdSchemaFileName) throws IntegrationValidationException {

        ZipEntry zipXmlFile = zipFile.getEntry(xmlFileName);
        if(zipXmlFile == null) {
            IntegrationResultErrorUnitDto resultError = new IntegrationResultErrorUnitDto(
                    null,
                    String.format(IntegrationResultLabel.FILE_NOT_FOUND, xmlFileName));
            throw new IntegrationValidationException(resultError);
        }

        try {
            InputStream templateStream = getClass().getClassLoader().getResourceAsStream("templates//" + xsdSchemaFileName);
            SchemaFactory facto = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            facto.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            facto.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            Source schemaSource = new StreamSource(templateStream);
            Schema schema = facto.newSchema(schemaSource);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(zipFile.getInputStream(zipXmlFile)));
        }
        catch(Exception ex) {
            IntegrationResultErrorUnitDto resultError = new IntegrationResultErrorUnitDto(null,
                    String.format(IntegrationResultLabel.FILE_INVALID, xmlFileName, ex.getMessage()));
            throw new IntegrationValidationException(resultError);
        }
    }

    @Override
    public Document buildDocument(InputStream xmlFileStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlFileStream);
    }
}
