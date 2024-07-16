package hu.progmasters.fundraiser.event;

import hu.progmasters.fundraiser.dto.outgoing.FundCompletedInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnGoalCompletedEvent extends ApplicationEvent {
    private final FundCompletedInfo fundCompletedInfo;
    private final String message;

    public OnGoalCompletedEvent(FundCompletedInfo fundCompletedInfo, String message) {
        super(fundCompletedInfo);
        this.fundCompletedInfo = fundCompletedInfo;
        this.message = message;
    }
}