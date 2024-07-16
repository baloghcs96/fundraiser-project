package hu.progmasters.fundraiser.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFundInfo {
    private Long transactionId;
    private String senderName;
    private String receiverName;
    private String sentAmount;
    private String receivedAmount;
    private String sentCurrency;
    private String receivedCurrency;
    private String transactionTime;
}
