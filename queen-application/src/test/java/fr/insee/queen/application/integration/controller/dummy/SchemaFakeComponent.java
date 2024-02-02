package fr.insee.queen.application.integration.controller.dummy;

import fr.insee.queen.application.integration.component.builder.schema.SchemaComponent;
import fr.insee.queen.application.integration.component.exception.IntegrationValidationException;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.zip.ZipFile;

public class SchemaFakeComponent implements SchemaComponent {
    @Override
    public void throwExceptionIfXmlDataFileNotValid(ZipFile zipFile, String xmlFileName, String xsdSchemaFileName) {

    }

    @Override
    public void throwExceptionIfDataFileNotExist(ZipFile zipFile, String fileName) throws IntegrationValidationException {

    }

    @Override
    public void throwExceptionIfJsonDataFileNotValid(ZipFile zipFile, String fileName, String schemaFileName) throws IntegrationValidationException {

    }

    @Override
    public Document buildDocument(InputStream xmlFileStream) {
        return null;
    }
}
