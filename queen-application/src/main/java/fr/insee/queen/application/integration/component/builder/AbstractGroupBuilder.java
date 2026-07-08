package fr.insee.queen.application.integration.component.builder;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import fr.insee.queen.application.integration.component.builder.schema.SchemaComponent;
import fr.insee.queen.application.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.service.IntegrationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class AbstractGroupBuilder<T> implements GroupBuilder {

    protected final SchemaComponent schemaComponent;
    protected final Validator validator;
    protected final IntegrationService integrationService;
    protected final ObjectMapper mapper;

    protected AbstractGroupBuilder(SchemaComponent schemaComponent, Validator validator,
                                   IntegrationService integrationService, ObjectMapper mapper) {
        this.schemaComponent = schemaComponent;
        this.validator = validator;
        this.integrationService = integrationService;
        this.mapper = mapper;
    }

    protected abstract String jsonFileName();
    protected abstract SchemaType schemaType();
    protected abstract Class<T> dataType();
    protected abstract List<Group> toGroups(T data,  Set<String> questionnaireIds);
    protected abstract String errorId(T data);

    @Override
    public final List<IntegrationResultUnitDto> build(ZipFile zf, Set<String> questionnaireIds) {
        T data;
        try {
            schemaComponent.throwExceptionIfJsonDataFileNotValid(zf, jsonFileName(), schemaType());
            ZipEntry entry = zf.getEntry(jsonFileName());
            data = mapper.readValue(zf.getInputStream(entry), dataType());
        } catch (IntegrationValidationException ex) {
            return List.of(ex.getResultError());
        } catch (JacksonException _) {
            return List.of(IntegrationResultUnitDto.integrationResultUnitError(
                    null, IntegrationResultLabel.JSON_PARSING_ERROR.formatted(jsonFileName())));
        } catch (IOException _) {
            return List.of(IntegrationResultUnitDto.integrationResultUnitError(
                    null, IntegrationResultLabel.ZIP_PARSING_ERROR.formatted(zf.getName())));
        }

        Set<ConstraintViolation<T>> violations = validator.validate(data);
        if (!violations.isEmpty()) {
            return List.of(IntegrationResultUnitDto.integrationResultUnitError(errorId(data), formatViolations(violations)));
        }

        List<IntegrationResultUnitDto> results = new ArrayList<>();
        for (Group group : toGroups(data,  questionnaireIds)) {
            IntegrationResult result = integrationService.create(group);
            results.add(IntegrationResultUnitDto.fromModel(result));
        }
        return results;
    }

    protected static <V> String formatViolations(Set<ConstraintViolation<V>> violations) {
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<V> violation : violations) {
            message.append(violation.getPropertyPath().toString())
                    .append(": ")
                    .append(violation.getMessage())
                    .append(". ");
        }
        return message.toString();
    }
}
