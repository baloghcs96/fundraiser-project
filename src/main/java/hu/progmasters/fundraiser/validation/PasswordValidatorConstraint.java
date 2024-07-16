package hu.progmasters.fundraiser.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidatorConstraint implements ConstraintValidator<PasswordValidator, String> {

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,20}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null && Pattern.matches(PASSWORD_PATTERN, password);
    }
}