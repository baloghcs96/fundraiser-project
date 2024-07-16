package hu.progmasters.fundraiser.dto.outgoing;

import hu.progmasters.fundraiser.domain.enumeration.Grade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleCreateInfo {

    private String item;

    private Integer priceInCoin;

    private Integer quantity;

    private Grade grade;
}
