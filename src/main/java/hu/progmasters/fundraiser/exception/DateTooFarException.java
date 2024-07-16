package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DateTooFarException extends RuntimeException {
    private final int monthBetween;
}
