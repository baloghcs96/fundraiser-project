package hu.progmasters.fundraiser.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleItemInfo {

    private Long itemId;

    private String item;

    private int priceInCoin;
}
