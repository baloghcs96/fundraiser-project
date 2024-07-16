package hu.progmasters.fundraiser.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class WholeHourValidatorConstraint implements ConstraintValidator<WholeHourValidator, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.getMinute() == 0;
    }
}
