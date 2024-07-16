package hu.progmasters.fundraiser.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WholeHourValidatorConstraint.class)
public @interface WholeHourValidator {

    String message() default "The time must be on the hour";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
