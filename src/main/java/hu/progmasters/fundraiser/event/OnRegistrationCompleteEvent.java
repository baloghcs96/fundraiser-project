package hu.progmasters.fundraiser.event;

import hu.progmasters.fundraiser.dto.outgoing.AccountInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private final AccountInfo accountInfo;
    private final Locale locale;
    private final String appUrl;

    public OnRegistrationCompleteEvent(AccountInfo accountInfo, String appUrl, Locale locale) {
        super(accountInfo);
        this.accountInfo = accountInfo;
        this.appUrl = appUrl;
        this.locale = locale;
    }
}