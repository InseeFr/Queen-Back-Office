package fr.insee.jms.validation;

import com.networknt.schema.ValidationMessage;
import lombok.Getter;

@Getter
public class SchemaValidationException extends Exception {
        private final java.util.Set<ValidationMessage> errors;
        public SchemaValidationException(String msg, java.util.Set<ValidationMessage> errors) { super(msg); this.errors = errors; }
}