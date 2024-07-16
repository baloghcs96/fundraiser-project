package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountAlreadyExistByEmailException extends RuntimeException {
    private final String email;
}
