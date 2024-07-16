package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Exchange;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.dto.outgoing.ExchangeInfo;
import hu.progmasters.fundraiser.repository.ExchangeRateRepository;
import hu.progmasters.fundraiser.util.MathUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class ExchangeService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final ExchangeRateRepository exchangeRateRepository;

    @Value("${scheduled.exchange.initial-delay}")
    private long initialDelay;

    @Value("${scheduled.exchange.fixed-delay}")
    private long fixedDelay;

    public ExchangeService(RestTemplate restTemplate, @Value("${fixer.api.url}") String apiUrl,
                           ExchangeRateRepository exchangeRateRepository) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public ExchangeInfo getAndSaveLatestRates() {
        ExchangeInfo response = restTemplate.getForObject(apiUrl, ExchangeInfo.class);
        saveLatestRates(response);
        if (response != null) {
            response.setRequestTime(LocalDateTime.now());
        }
        return response;
    }


    public Exchange getLatestExchangeRate() {
        return exchangeRateRepository.findFirstByOrderByLocalDateTimeDesc();
    }

    public double getRateByCurrency(Currency currency) {
        Exchange latestExchangeRate = getLatestExchangeRate();
        switch (currency) {
            case USD:
                return latestExchangeRate.getUsdCurrency();
            case HUF:
                return latestExchangeRate.getHufCurrency();
            default:
                throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }

    public double convertToEur(Currency baseCurrency, double amount) {
        if (baseCurrency == Currency.EUR) {
            return amount;
        } else {
            return amount / getRateByCurrency(baseCurrency);
        }
    }

    public double convert(Currency baseCurrency, Currency targetCurrency, double amount) {
        if (baseCurrency == targetCurrency) {
            return amount;
        } else {
            double amountInEur = convertToEur(baseCurrency, amount);
            return MathUtil.roundToTwoDecimalPlaces(amountInEur * getRateByCurrency(targetCurrency));

        }
    }

    public boolean isExchangeDataEmpty() {
        return exchangeRateRepository.count() == 0;
    }

    public void initializeExchangeDataIfNeeded() {
        if (isExchangeDataEmpty()) {
            getAndSaveLatestRates();
        }
    }

    @Scheduled(initialDelayString = "${scheduled.exchange.initial-delay}",
            fixedDelayString = "${scheduled.exchange.fixed-delay}")
    public void scheduledGetAndSaveLatestRates() {
        getAndSaveLatestRates();
    }

    private void saveLatestRates(ExchangeInfo response) {
        if (response != null && response.getRates() != null) {
            Exchange exchangeRate = new Exchange();
            exchangeRate.setBaseCurrency(response.getBase());
            exchangeRate.setLocalDateTime(LocalDateTime.now());
            exchangeRate.setUsdCurrency(response.getRates().get("USD"));
            exchangeRate.setHufCurrency(response.getRates().get("HUF"));
            exchangeRateRepository.save(exchangeRate);
        }
    }
}