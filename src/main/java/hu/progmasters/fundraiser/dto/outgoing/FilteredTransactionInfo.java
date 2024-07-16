package hu.progmasters.fundraiser.dto.outgoing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonPropertyOrder({
        "targetFundTitle", "sentAmount", "sentCurrency", "receivedAmount", "receivedCurrency", "transactionTime"})
public class FilteredTransactionInfo {

    private String targetFundTitle;

    private Double sentAmount;

    private Currency SentCurrency;

    private Double receivedAmount;

    private Currency ReceivedCurrency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime transactionTime;
}
