package fr.insee.queen.application.integration.component.builder.schema;

import fr.insee.queen.application.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.application.web.validation.json.SchemaType;

import java.util.zip.ZipFile;


public interface SchemaComponent {
    void throwExceptionIfDataFileNotExist(ZipFile zipFile, String fileName) throws IntegrationValidationException;

    void throwExceptionIfJsonDataFileNotValid(ZipFile zipFile, String fileName, SchemaType schemaType) throws IntegrationValidationException;
}
