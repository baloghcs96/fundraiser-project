package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountAlreadyExistByNameException extends RuntimeException {
    private final String accountName;
}
