package hu.progmasters.fundraiser.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFilterCommand {

    private String fundTitle;
    private Double sentAmount;
    private LocalDate transactionDate;

}