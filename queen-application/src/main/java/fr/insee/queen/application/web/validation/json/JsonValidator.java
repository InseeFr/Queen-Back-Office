package fr.insee.queen.application.web.validation.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ValidationMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

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

        Set<ValidationMessage> errors = validatorComponent.validate(schemaType, jsonNode);

        if(errors.isEmpty()) {
            return true;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for(ValidationMessage errorMessage : errors) {
            messageBuilder.append(errorMessage.getMessage());
            messageBuilder.append("\n");
        }
        log.error(messageBuilder.toString());
        return false;
    }
}

