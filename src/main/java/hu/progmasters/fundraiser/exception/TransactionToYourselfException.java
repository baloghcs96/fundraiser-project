package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionToYourselfException extends RuntimeException {
    Long accountId;
    Long fundId;
}
