package hu.progmasters.fundraiser.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseInfo {

    private String item;

    private int priceInCoin;

    private int quantity;

    private LocalDateTime purchaseTime;
}
