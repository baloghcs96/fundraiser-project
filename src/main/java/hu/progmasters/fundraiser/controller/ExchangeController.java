package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.dto.outgoing.ExchangeInfo;
import hu.progmasters.fundraiser.service.ExchangeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fundraiser/exchange")
@AllArgsConstructor
public class ExchangeController {

    private final ExchangeService fixerService;

    @GetMapping("/latest")
    public ExchangeInfo getAndSaveLatestRates() {
        return fixerService.getAndSaveLatestRates();
    }

}
