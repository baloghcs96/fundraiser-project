package hu.progmasters.fundraiser.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidatorConstraint implements ConstraintValidator<EmailValidator, String> {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@.+\\..{2,}$";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && Pattern.matches(EMAIL_PATTERN, email);
    }
}