package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountVerificationTokenNotFoundByAccountIdException extends RuntimeException {
    private final Long accountId;
}
