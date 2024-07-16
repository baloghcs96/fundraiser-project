package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountAlreadyVerifiedException extends RuntimeException {
    private final String email;
}
