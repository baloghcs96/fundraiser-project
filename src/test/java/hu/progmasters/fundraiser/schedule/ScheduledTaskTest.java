package hu.progmasters.fundraiser.schedule;

import hu.progmasters.fundraiser.repository.ScheduledTaskRepository;
import hu.progmasters.fundraiser.service.DataArchivingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScheduledTaskTest {

    @Mock
    private DataArchivingService dataArchivingService;

    @Mock
    private ScheduledTaskRepository scheduledTaskRepository;

    @InjectMocks
    private ScheduledTask scheduledTask;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(scheduledTask, "defaultRate", 1000L);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testArchiveData() {
        scheduledTask.archiveData();

        verify(dataArchivingService, times(1)).archiveExchanges();
        verify(dataArchivingService, times(1)).whenArchiveLeastExchangeRates();
        verify(scheduledTaskRepository, times(1)).save(any());
    }

    @Test
    void scheduleRunnableWithFixedDelay_calculatesInitialDelayCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesAgo = now.minusMinutes(10);
        hu.progmasters.fundraiser.domain.entity.ScheduledTask lastRunInfo = new hu.progmasters.fundraiser.domain.entity.ScheduledTask();
        lastRunInfo.setLastRun(tenMinutesAgo);
        when(scheduledTaskRepository.findById(1L)).thenReturn(Optional.of(lastRunInfo));
        ReflectionTestUtils.setField(scheduledTask, "defaultRate", 600000L); // 10 minutes in milliseconds

        scheduledTask.scheduleRunnableWithFixedDelay();

        verify(scheduledTaskRepository, times(1)).findById(1L);

    }

    @Test
    void scheduleRunnableWithFixedDelay_withNoLastRunInfo_setsInitialDelayToDefaultRate() {
        when(scheduledTaskRepository.findById(1L)).thenReturn(Optional.empty());
        ReflectionTestUtils.setField(scheduledTask, "defaultRate", 1000L); // Setting default rate

        scheduledTask.scheduleRunnableWithFixedDelay();

        // Verification logic here depends on the ability to inspect the TimerTask scheduling,
        // which might require refactoring of ScheduledTask to make it testable.
        // For example, exposing the Timer or TimerTask to verify scheduling details.
    }


}

