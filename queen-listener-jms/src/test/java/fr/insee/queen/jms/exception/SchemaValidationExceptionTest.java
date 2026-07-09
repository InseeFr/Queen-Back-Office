package fr.insee.queen.jms.exception;

import com.networknt.schema.Error;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchemaValidationExceptionTest {

    @Test
    void constructor_storesMessage_andErrorsReference() {
        // We don't use ValidationMessage in this test: raw Set type.
        List<Error> errors = new ArrayList<>(); // empty but non-null

        String msg = "schema invalid";
        SchemaValidationException ex = new SchemaValidationException(msg, errors);

        assertEquals(msg, ex.getMessage(), "The message should be kept as-is");
        assertSame(errors, ex.getErrors(), "The errors Set should be the same reference (no defensive copy)");
    }

    @Test
    void getErrors_canBeNull_ifConstructedWithNull() {
        SchemaValidationException ex = new SchemaValidationException("oops", null);
        assertNull(ex.getErrors(), "getErrors() can be null if constructed with null");
    }

    @Test
    void stackTrace_andToString_areAvailable() {
        SchemaValidationException ex = new SchemaValidationException("boom", null);
        // simple sanity checks
        assertNotNull(ex.toString());
        assertNotNull(ex.getStackTrace());
    }
}
