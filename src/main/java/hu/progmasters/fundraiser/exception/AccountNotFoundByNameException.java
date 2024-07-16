package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountNotFoundByNameException extends RuntimeException {
    private final String name;
}
