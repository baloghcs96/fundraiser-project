package hu.progmasters.fundraiser.dto.incoming;

import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.validation.EmailValidator;
import hu.progmasters.fundraiser.validation.EnumValidator;
import hu.progmasters.fundraiser.validation.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSaveUpdateCommand {

    @NotBlank(message = "Account name must not be blank")
    private String accountName;

    @PasswordValidator
    private String password;

    @EmailValidator
    private String email;

    @EnumValidator(enumClass = Currency.class)
    private String currency;

}
