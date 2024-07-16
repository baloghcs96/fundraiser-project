package hu.progmasters.fundraiser.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeInfo {

    private boolean success;
    private long timestamp;
    private String base;
    private String date;
    private LocalDateTime requestTime;
    private Map<String, Double> rates;

}
