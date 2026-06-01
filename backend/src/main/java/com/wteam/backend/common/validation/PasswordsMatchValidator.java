package com.wteam.backend.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {
    private String originalField;
    private String checkField;

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
        this.originalField = constraintAnnotation.originalPassword();
        this.checkField = constraintAnnotation.checkPassword();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Object originalValue = value.getClass().getMethod(originalField).invoke(value);
            Object checkValue = value.getClass().getMethod(checkField).invoke(value);

            if (originalValue == null && checkValue == null) {
                return true;
            }

            if (originalValue == null || checkValue == null) {
                return false;
            }

            return originalValue.equals(checkValue);
        } catch (Exception e) {
            return false;
        }
    }
}
