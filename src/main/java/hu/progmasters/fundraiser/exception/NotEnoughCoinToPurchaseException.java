package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotEnoughCoinToPurchaseException extends RuntimeException {
    private final int accountCoin;
}
