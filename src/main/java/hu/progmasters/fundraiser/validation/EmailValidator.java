package hu.progmasters.fundraiser.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidatorConstraint.class)
public @interface EmailValidator {

    String message() default "Invalid email. Email must be a valid email address.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}