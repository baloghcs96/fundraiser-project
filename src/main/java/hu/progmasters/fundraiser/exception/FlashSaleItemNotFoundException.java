package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FlashSaleItemNotFoundException extends RuntimeException {
    private final Long itemId;
}
