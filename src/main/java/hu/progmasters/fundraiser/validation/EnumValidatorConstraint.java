package hu.progmasters.fundraiser.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidatorConstraint implements ConstraintValidator<EnumValidator, String> {

    Set<String> values;
    private String enumValues;
    private String enumClassName;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        values = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
        enumValues = values.toString();
        enumClassName = constraintAnnotation.enumClass().getSimpleName();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!values.contains(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(enumClassName + " must be one of the following: " + enumValues)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}