package hu.progmasters.fundraiser.dto.incoming;

import hu.progmasters.fundraiser.validation.EmailValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountGetEmailForForgottenPasswordCommand {

    @EmailValidator
    private String email;

}
