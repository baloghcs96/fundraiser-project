package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionVerificationTokenNotFoundByTokenException extends RuntimeException {
    private final String token;
}
