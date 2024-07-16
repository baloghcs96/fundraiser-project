package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.dto.outgoing.ChooseCurrency;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyServiceTest {

    @Test
    public void testGetAvailableCurrencies() {
        CurrencyService currencyService = new CurrencyService();
        ChooseCurrency chooseCurrency = currencyService.getAvailableCurrencies();
        List<String> currency = chooseCurrency.getCurrency();
        List<String> currencyList = List.of(currency.toString().substring(2, currency.toString().length() - 2).split(", "));

        System.out.println(currencyList.get(0));
        System.out.println(currencyList.get(1));
        System.out.println(currencyList.get(2));
        assertEquals(3, currencyList.size());
        assertTrue(currencyList.contains("HUF"));
        assertTrue(currencyList.contains("USD"));
        assertTrue(currencyList.contains("EUR"));

    }
}