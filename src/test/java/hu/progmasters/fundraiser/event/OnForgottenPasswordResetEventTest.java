package hu.progmasters.fundraiser.event;

import hu.progmasters.fundraiser.dto.outgoing.AccountInfo;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnForgottenPasswordResetEventTest {

    @Test
    void testEventInitialization() {
        AccountInfo accountInfo = new AccountInfo(); // Assume AccountInfo is properly instantiated
        String appUrl = "http://example.com";
        Locale locale = Locale.ENGLISH;

        OnForgottenPasswordResetEvent event = new OnForgottenPasswordResetEvent(accountInfo, appUrl, locale);

        assertEquals(accountInfo, event.getAccountInfo());
        assertEquals(appUrl, event.getAppUrl());
        assertEquals(locale, event.getLocale());
    }

}