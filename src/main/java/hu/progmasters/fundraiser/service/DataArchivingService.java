package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Exchange;
import hu.progmasters.fundraiser.domain.entity.ScheduledTask;
import hu.progmasters.fundraiser.repository.ExchangeRateRepository;
import hu.progmasters.fundraiser.repository.ScheduledTaskRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class DataArchivingService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ScheduledTaskRepository scheduledTaskRepository;


    public DataArchivingService(ExchangeRateRepository exchangeRateRepository,
                                ScheduledTaskRepository scheduledTaskRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.scheduledTaskRepository = scheduledTaskRepository;
    }

    public void archiveExchanges() {
        List<Exchange> exchanges = exchangeRateRepository.findAll();
        boolean writeSuccessful = false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("exchangeArchive.txt", true))) {
            for (Exchange exchange : exchanges) {
                writer.write(exchange.toString());
                writer.newLine();
            }
            writeSuccessful = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (writeSuccessful) {
            exchangeRateRepository.deleteAll();
        }
    }

    public void whenArchiveLeastExchangeRates() {
        List<ScheduledTask> scheduledTasks = scheduledTaskRepository.findAll();
        boolean writeSuccessful = false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("leastExchangeArchive.txt", true))) {
            for (ScheduledTask scheduledTask : scheduledTasks) {
                writer.write(scheduledTask.toString());
                writer.newLine();
            }
            writeSuccessful = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (writeSuccessful) {
            scheduledTaskRepository.deleteAll();
        }
    }
}
