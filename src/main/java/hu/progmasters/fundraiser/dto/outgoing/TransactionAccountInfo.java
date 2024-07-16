package hu.progmasters.fundraiser.dto.outgoing;

import hu.progmasters.fundraiser.domain.enumeration.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionAccountInfo {

    private String targetFundTitle;

    private Double sentAmount;

    private Currency SentCurrency;

    private String sentCurrencySymbol;

    private String receivedCurrencySymbol;
}
