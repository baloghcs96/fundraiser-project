package hu.progmasters.fundraiser.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonatorInfo {

    private Long accountId;
    private String accountName;
    private Double sentAmount;

}
