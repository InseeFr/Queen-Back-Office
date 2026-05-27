package fr.insee.queen.application.web.validation.json;

import com.networknt.schema.Error;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JsonValidator implements ConstraintValidator<JsonValid, JsonNode> {
    private final JsonValidatorComponent validatorComponent;
    private SchemaType schemaType;

    @Override
    public void initialize(JsonValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        schemaType = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(JsonNode jsonNode, ConstraintValidatorContext cxt) {
        if (jsonNode == null) {
            return true;
        }

        List<Error> errors = validatorComponent.validate(schemaType, jsonNode);

        if(errors.isEmpty()) {
            return true;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for(Error errorMessage : errors) {
            messageBuilder.append(errorMessage.getMessage());
            messageBuilder.append("\n");
        }
        log.error(messageBuilder.toString());
        return false;
    }
}

