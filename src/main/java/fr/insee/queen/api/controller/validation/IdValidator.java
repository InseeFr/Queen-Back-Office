package fr.insee.queen.api.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IdValidator implements ConstraintValidator<IdValid, String>
{
    @Override
    public void initialize(IdValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String identifier, ConstraintValidatorContext cxt) {
        System.out.println("TEST");
        if(identifier == null) {
            return false;
        }
        return Pattern.matches("[A-Za-z0-9\\-]+", identifier);
    }
}
