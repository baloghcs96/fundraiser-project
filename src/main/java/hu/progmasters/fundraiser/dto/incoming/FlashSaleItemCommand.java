package hu.progmasters.fundraiser.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleItemCommand {

    @NotNull
    Long accountId;

}
