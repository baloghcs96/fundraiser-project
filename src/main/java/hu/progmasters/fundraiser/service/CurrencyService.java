package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.dto.outgoing.ChooseCurrency;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class CurrencyService {

    public ChooseCurrency getAvailableCurrencies() {
        return new ChooseCurrency(List.of(Arrays.toString(Currency.values())));
    }
}
