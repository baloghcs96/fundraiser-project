package hu.progmasters.fundraiser.dto.incoming;

import hu.progmasters.fundraiser.domain.enumeration.Grade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleItemSaveUpdateCommand {

    @NotBlank
    private String item;

    @NotNull
    private Integer priceInCoin;

    @NotNull
    private Integer quantity;

    private Grade grade;

}
