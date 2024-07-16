package hu.progmasters.fundraiser.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceUpdateCommand {

    @Positive(message = "You can only update your balance with a positive number!")
    private Double balance;

}
