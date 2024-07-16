package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Exchange;
import hu.progmasters.fundraiser.domain.entity.ScheduledTask;
import hu.progmasters.fundraiser.repository.ExchangeRateRepository;
import hu.progmasters.fundraiser.repository.ScheduledTaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataArchivingServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ScheduledTaskRepository scheduledTaskRepository;

    @InjectMocks
    private DataArchivingService dataArchivingService;

    private AutoCloseable closeable;


    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void archiveExchangesTest() {
        List<Exchange> exchanges = Arrays.asList(
                new Exchange(),
                new Exchange()
        );
        when(exchangeRateRepository.findAll()).thenReturn(exchanges);

        dataArchivingService.archiveExchanges();

        verify(exchangeRateRepository).deleteAll();
    }

    @Test
    void whenArchiveLeastExchangeRatesTest() {
        List<ScheduledTask> scheduledTasks = Arrays.asList(
                new ScheduledTask(),
                new ScheduledTask()
        );
        when(scheduledTaskRepository.findAll()).thenReturn(scheduledTasks);

        dataArchivingService.whenArchiveLeastExchangeRates();

        verify(scheduledTaskRepository).deleteAll();
    }

}