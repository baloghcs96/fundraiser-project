package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Exchange;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.dto.outgoing.ExchangeInfo;
import hu.progmasters.fundraiser.repository.ExchangeRateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExchangeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeService exchangeService;


    @Value("${fixer.api.url}")
    private String apiUrl;

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
    void testGetAndSaveLatestRates() {
        ExchangeInfo expectedResponse = new ExchangeInfo();
        expectedResponse.setBase("EUR");
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.2);
        rates.put("HUF", 300.0);
        expectedResponse.setRates(rates);

        when(restTemplate.getForObject(apiUrl, ExchangeInfo.class)).thenReturn(expectedResponse);

        ExchangeInfo actualResponse = exchangeService.getAndSaveLatestRates();

        verify(exchangeRateRepository, times(1)).save(any(Exchange.class));

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getBase(), actualResponse.getBase());
        assertEquals(expectedResponse.getRates(), actualResponse.getRates());
    }


    @Test
    void testGetAndSaveLatestRatesWithNullResponse() {
        when(restTemplate.getForObject(anyString(), eq(ExchangeInfo.class))).thenReturn(null);

        ExchangeInfo actualResponse = exchangeService.getAndSaveLatestRates();

        verify(exchangeRateRepository, times(0)).save(any(Exchange.class));

        assertNull(actualResponse);
    }

    @Test
    void testGetAndSaveLatestRatesWithNullRates() {
        ExchangeInfo responseWithNullRates = new ExchangeInfo();
        responseWithNullRates.setBase("EUR");
        responseWithNullRates.setRates(null);

        when(restTemplate.getForObject(apiUrl, ExchangeInfo.class)).thenReturn(responseWithNullRates);

        ExchangeInfo actualResponse = exchangeService.getAndSaveLatestRates();

        verify(exchangeRateRepository, times(0)).save(any(Exchange.class));
        assertNotNull(actualResponse);
        assertNull(actualResponse.getRates());
    }

    @Test
    void testIsExchangeDataEmptyWhenEmpty() {
        when(exchangeRateRepository.count()).thenReturn(0L);

        assertTrue(exchangeService.isExchangeDataEmpty());
    }

    @Test
    void testIsExchangeDataEmptyWhenNotEmpty() {
        when(exchangeRateRepository.count()).thenReturn(1L);

        assertFalse(exchangeService.isExchangeDataEmpty());
    }


    @Test
    void testGetLatestExchangeRate() {
        Exchange mockExchange = new Exchange();
        mockExchange.setBaseCurrency("EUR");
        mockExchange.setLocalDateTime(LocalDateTime.now());
        mockExchange.setUsdCurrency(1.2);
        mockExchange.setHufCurrency(300.0);

        when(exchangeRateRepository.findFirstByOrderByLocalDateTimeDesc()).thenReturn(mockExchange);

        Exchange result = exchangeService.getLatestExchangeRate();

        assertNotNull(result);
        assertEquals("EUR", result.getBaseCurrency());
        assertEquals(1.2, result.getUsdCurrency());
        assertEquals(300.0, result.getHufCurrency());
    }

    @Test
    void testGetRateByCurrency() {
        Exchange mockExchange = new Exchange();
        mockExchange.setUsdCurrency(1.2);
        mockExchange.setHufCurrency(300.0);

        when(exchangeRateRepository.findFirstByOrderByLocalDateTimeDesc()).thenReturn(mockExchange);

        double usdRate = exchangeService.getRateByCurrency(Currency.USD);
        double hufRate = exchangeService.getRateByCurrency(Currency.HUF);

        assertEquals(1.2, usdRate);
        assertEquals(300.0, hufRate);
    }

    @Test
    void testConvertToEur() {
        Exchange mockExchange = new Exchange();
        mockExchange.setUsdCurrency(1.2);
        mockExchange.setHufCurrency(300.0);

        when(exchangeRateRepository.findFirstByOrderByLocalDateTimeDesc()).thenReturn(mockExchange);

        double convertedUsdToEur = exchangeService.convertToEur(Currency.USD, 100);
        assertEquals(83.33, convertedUsdToEur, 0.01);

        double convertedHufToEur = exchangeService.convertToEur(Currency.HUF, 30000);
        assertEquals(100, convertedHufToEur, 0.01);

        double convertedEurToEur = exchangeService.convertToEur(Currency.EUR, 100);
        assertEquals(100, convertedEurToEur, 0.01);
    }

    @Test
    void testConvertDifferentCurrencies() {
        Exchange mockExchange = new Exchange();
        mockExchange.setUsdCurrency(1.2);
        mockExchange.setHufCurrency(300.0);
        when(exchangeRateRepository.findFirstByOrderByLocalDateTimeDesc()).thenReturn(mockExchange);

        double convertedUsdToHuf = exchangeService.convert(Currency.USD, Currency.HUF, 100);
        assertEquals(25000, convertedUsdToHuf, 0.01);

        double convertedHufToUsd = exchangeService.convert(Currency.HUF, Currency.USD, 30000);
        assertEquals(120, convertedHufToUsd, 0.01);
    }

    @Test
    void testInitializeExchangeDataIfNeededWithData() {
        when(exchangeRateRepository.count()).thenReturn(1L);

        exchangeService.initializeExchangeDataIfNeeded();

        verify(restTemplate, times(0)).getForObject(anyString(), eq(ExchangeInfo.class));
    }

}