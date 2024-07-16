package hu.progmasters.fundraiser.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSaveUpdateCommand {

    @NotNull(message = "Fund id must not be null")
    private Long fundId;

    @Positive(message = "Sent amount must be positive")
    //TODO: validate the sent amount
//    @NumberFormat(pattern = "0.00")
//    @Pattern(regexp = "\\d+", message = "Sent amount must be a number")
    private Double sentAmount;

    //TODO a tranzakció időpontja minimum local date +1 óra, hogy ne tudjon lejárni tranzakció

    //    @WholeHour
    private LocalDateTime transactionTime;

}
