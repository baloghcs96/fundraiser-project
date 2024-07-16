package hu.progmasters.fundraiser.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidatorConstraint.class)
public @interface PasswordValidator {

    String message() default "Invalid password. Password must be 8-20 characters long, contain at least 1 uppercase letter, 1 lowercase letter and 1 special character.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}