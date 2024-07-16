package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotEnoughBalanceToTransferException extends RuntimeException {
    private final Double balance;
}
