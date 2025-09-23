// src/main/java/com/example/validation/IdValidator.java
package fr.insee.jms.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IdValidator implements ConstraintValidator<IdValid, String> {
  @Override public boolean isValid(String value, ConstraintValidatorContext ctx) {
    if (value == null || value.isBlank()) return false;
    // Ex: 8 à 12 alphanumériques (adapte à ta règle INSEE)
    return value.matches("^[A-Z0-9]{8,12}$");
  }
}
