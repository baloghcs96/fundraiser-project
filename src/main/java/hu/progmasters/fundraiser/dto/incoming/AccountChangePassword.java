package hu.progmasters.fundraiser.dto.incoming;

import hu.progmasters.fundraiser.validation.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountChangePassword {

    @PasswordValidator
    private String newPassword;

}
