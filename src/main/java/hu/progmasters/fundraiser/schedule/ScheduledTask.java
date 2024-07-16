package hu.progmasters.fundraiser.schedule;

import hu.progmasters.fundraiser.repository.ScheduledTaskRepository;
import hu.progmasters.fundraiser.service.DataArchivingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ScheduledTask {

    private final DataArchivingService dataArchivingService;
    private final ScheduledTaskRepository scheduledTaskRepository;

    @Value("${scheduled.task.default.rate}")
    private long defaultRate;

    public ScheduledTask(DataArchivingService dataArchivingService, ScheduledTaskRepository scheduledTaskRepository) {
        this.dataArchivingService = dataArchivingService;
        this.scheduledTaskRepository = scheduledTaskRepository;
    }

    @PostConstruct
    public void scheduleRunnableWithFixedDelay() {
        hu.progmasters.fundraiser.domain.entity.ScheduledTask lastRunInfo = scheduledTaskRepository.findById(1L).orElse(null);
        long initialDelay = defaultRate;

        if (lastRunInfo != null) {
            LocalDateTime lastRunTime = lastRunInfo.getLastRun();
            long elapsedTimeSinceLastRun = ChronoUnit.MILLIS.between(lastRunTime, LocalDateTime.now());

            if (elapsedTimeSinceLastRun < defaultRate) {
                initialDelay = defaultRate - elapsedTimeSinceLastRun;
            }
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                archiveData();
                scheduleRunnableWithFixedDelay();
            }
        }, initialDelay);
    }

    public void archiveData() {
        dataArchivingService.archiveExchanges();
        dataArchivingService.whenArchiveLeastExchangeRates();
        hu.progmasters.fundraiser.domain.entity.ScheduledTask taskInfo = new hu.progmasters.fundraiser.domain.entity.ScheduledTask();
        taskInfo.setLastRun(LocalDateTime.now());
        scheduledTaskRepository.save(taskInfo);
    }
}
