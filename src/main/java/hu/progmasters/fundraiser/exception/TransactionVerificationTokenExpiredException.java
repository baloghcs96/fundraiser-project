package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionVerificationTokenExpiredException extends RuntimeException {
    private final String token;
}
