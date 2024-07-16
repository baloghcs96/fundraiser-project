package hu.progmasters.fundraiser.exception;

import hu.progmasters.fundraiser.domain.enumeration.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FundNotFoundByCategoryException extends RuntimeException {
    private final Category category;
}
