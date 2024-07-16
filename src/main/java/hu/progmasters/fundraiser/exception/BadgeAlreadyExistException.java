package hu.progmasters.fundraiser.exception;

import hu.progmasters.fundraiser.domain.enumeration.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadgeAlreadyExistException extends RuntimeException {
    private final Grade grade;
}
