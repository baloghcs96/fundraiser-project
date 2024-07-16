package hu.progmasters.fundraiser.event;

import hu.progmasters.fundraiser.dto.outgoing.TransactionCreateInfo;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnTransactionCreateCompleteEventTest {

    @Test
    void testEventInitialization() {
        TransactionCreateInfo transactionCreateInfo = new TransactionCreateInfo(); // Feltételezzük, hogy a TransactionCreateInfo megfelelően van inicializálva
        String appUrl = "http://example.com";
        Locale locale = Locale.ENGLISH;

        OnTransactionCreateCompleteEvent event = new OnTransactionCreateCompleteEvent(transactionCreateInfo, appUrl, locale);

        assertEquals(transactionCreateInfo, event.getTransactionCreateInfo());
        assertEquals(appUrl, event.getAppUrl());
        assertEquals(locale, event.getLocale());
    }

}