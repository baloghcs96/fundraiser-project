package hu.progmasters.fundraiser.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InvalidScheduleTimeException extends RuntimeException {
    private final LocalDateTime scheduleTime;
}
