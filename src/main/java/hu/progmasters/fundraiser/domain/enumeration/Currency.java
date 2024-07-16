package hu.progmasters.fundraiser.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    HUF("Ft"),
    EUR("€"),
    USD("$");
    private final String symbol;
}
