package fr.insee.queen.application.integration.component.builder.schema;

import com.networknt.schema.Error;
import fr.insee.queen.application.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.web.validation.json.JsonValidatorComponent;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


@Component
@RequiredArgsConstructor
public class SchemaIntegrationComponent implements SchemaComponent {

    private final ObjectMapper mapper;
    private final JsonValidatorComponent jsonValidator;

    @Override
    public void throwExceptionIfDataFileNotExist(ZipFile zipFile, String fileName) throws IntegrationValidationException {
        ZipEntry zipEntry = zipFile.getEntry(fileName);
        if (zipEntry == null) {
            IntegrationResultUnitDto resultError = IntegrationResultUnitDto.integrationResultUnitError(
                    null,
                    String.format(IntegrationResultLabel.FILE_NOT_FOUND, fileName));
            throw new IntegrationValidationException(resultError);
        }
    }

    @Override
    public void throwExceptionIfJsonDataFileNotValid(ZipFile zipFile, String fileName, SchemaType schemaType) throws IntegrationValidationException {
        throwExceptionIfDataFileNotExist(zipFile, fileName);
        ZipEntry zipJsonFile = zipFile.getEntry(fileName);

        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(zipFile.getInputStream(zipJsonFile));
        } catch (JacksonException ex) {
            IntegrationResultUnitDto resultError = IntegrationResultUnitDto.integrationResultUnitError(null,
                    IntegrationResultLabel.FILE_INVALID.formatted(fileName, ex.getMessage()));
            throw new IntegrationValidationException(resultError);
        } catch (IOException e) {
            IntegrationResultUnitDto resultError = IntegrationResultUnitDto.integrationResultUnitError(null,
                    (IntegrationResultLabel.ZIP_PARSING_ERROR + "%nError message: %s").formatted(fileName, e.getMessage()));
            throw new IntegrationValidationException(resultError);
        }

        List<Error> errors = jsonValidator.validate(schemaType, jsonNode);
        if(errors.isEmpty()) {
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for(Error errorMessage : errors) {
            messageBuilder.append(errorMessage.getMessage());
            messageBuilder.append(". ");
        }

        IntegrationResultUnitDto resultError = IntegrationResultUnitDto.integrationResultUnitError(null,
                String.format(IntegrationResultLabel.FILE_INVALID, fileName, messageBuilder));
        throw new IntegrationValidationException(resultError);
    }
}
