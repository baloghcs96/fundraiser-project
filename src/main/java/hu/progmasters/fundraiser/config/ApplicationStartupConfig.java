package hu.progmasters.fundraiser.config;

import hu.progmasters.fundraiser.service.BadgeService;
import hu.progmasters.fundraiser.service.ExchangeService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ApplicationStartupConfig {

    private final BadgeService badgeService;
    private final ExchangeService exchangeService;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        badgeService.initializeBadgesIfNeeded();
        exchangeService.initializeExchangeDataIfNeeded();
    }

}
