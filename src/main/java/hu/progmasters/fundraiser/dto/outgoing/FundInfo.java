package hu.progmasters.fundraiser.dto.outgoing;

import hu.progmasters.fundraiser.domain.enumeration.Category;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundInfo {

    private Long fundId;
    private String title;
    private Category category;
    private Currency currency;
    private String description;
    private Double goalAmount;
    private Double currentAmount;
    private Double progress;
    private List<String> images;
    private LocalDateTime closeDate;


}
