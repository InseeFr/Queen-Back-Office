package fr.insee.queen.jms.exception;

import com.networknt.schema.Error;
import lombok.Getter;

@Getter
public class SchemaValidationException extends Exception {
        private final java.util.List<Error> errors;
        public SchemaValidationException(String msg, java.util.List<Error> errors) { super(msg); this.errors = errors; }
}