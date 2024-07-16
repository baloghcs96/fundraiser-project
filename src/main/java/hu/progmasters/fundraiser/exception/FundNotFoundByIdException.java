package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FundNotFoundByIdException extends RuntimeException {
    private final Long id;
}
