package hu.progmasters.fundraiser.dto.outgoing;

import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {

    private Long accountId;
    private String accountName;
    private String email;
    private Currency currency;
    private Grade grade;
    private Double balance;
    private boolean isVerified;
    private String badge;

}
