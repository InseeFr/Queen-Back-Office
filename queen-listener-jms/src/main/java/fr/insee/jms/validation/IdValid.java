// src/main/java/com/example/validation/IdValid.java
package fr.insee.jms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdValidator.class)
@Documented
public @interface IdValid {
  String message() default "Invalid id format";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
