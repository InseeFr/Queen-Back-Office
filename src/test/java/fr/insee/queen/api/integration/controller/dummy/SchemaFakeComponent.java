package fr.insee.queen.api.integration.controller.dummy;

import fr.insee.queen.api.integration.controller.component.builder.schema.SchemaComponent;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.zip.ZipFile;

public class SchemaFakeComponent implements SchemaComponent {
    @Override
    public void throwExceptionIfXmlDataFileNotValid(ZipFile zipFile, String xmlFileName, String xsdSchemaFileName) {

    }

    @Override
    public Document buildDocument(InputStream xmlFileStream) {
        return null;
    }
}
