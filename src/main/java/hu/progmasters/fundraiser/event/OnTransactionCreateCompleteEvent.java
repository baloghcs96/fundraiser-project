package hu.progmasters.fundraiser.event;

import hu.progmasters.fundraiser.dto.outgoing.TransactionCreateInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
public class OnTransactionCreateCompleteEvent extends ApplicationEvent {
    private final TransactionCreateInfo transactionCreateInfo;
    private final Locale locale;
    private final String appUrl;

    public OnTransactionCreateCompleteEvent(TransactionCreateInfo transactionCreateInfo, String appUrl, Locale locale) {
        super(transactionCreateInfo);
        this.transactionCreateInfo = transactionCreateInfo;
        this.appUrl = appUrl;
        this.locale = locale;
    }
}