package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Fund;
import hu.progmasters.fundraiser.domain.entity.Income;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.repository.IncomeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

class IncomeServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private FundService fundService;

    @InjectMocks
    private IncomeService incomeService;

    private AutoCloseable closeable;

    private Fund fund1;

    private Fund fund2;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        fund1 = new Fund();
        fund1.setTitle("Fund1");
        fund1.setFundId(1L);
        fund1.setCurrency(Currency.EUR);
        fund1.setGoalAmount(1000.0);
        fund1.setCurrentAmount(0.0);

        fund2 = new Fund();
        fund2.setTitle("Fund2");
        fund2.setFundId(1L);
        fund2.setCurrency(Currency.EUR);
        fund2.setGoalAmount(1000.0);
        fund2.setCurrentAmount(0.0);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testCalculateAndSaveIncomeForCompletedFunds() {
        Fund completedFund = new Fund();
        completedFund.setFundId(1L);
        completedFund.setCurrency(Currency.EUR);
        completedFund.setGoalAmount(1000.0);
        completedFund.setCurrentAmount(1000.0);
        List<Fund> completedFunds = List.of(completedFund);

        when(fundService.findCompletedFundsNotProcessedWithCommission()).thenReturn(completedFunds);
        when(fundService.calculateCommission(anyDouble())).thenReturn(50.0);

        incomeService.calculateAndSaveIncomeForCompletedFunds();

        verify(fundService, times(1)).findCompletedFundsNotProcessedWithCommission();
        verify(incomeRepository, times(1)).save(any(Income.class));
        verify(fundService, times(1)).calculateCommission(1000.0);
    }

    @Test
    void testCalculateAndSaveIncomeForCompletedFunds_WithNoCompletedFunds() {
        List<Fund> emptyList = Collections.emptyList();
        when(fundService.findCompletedFundsNotProcessedWithCommission()).thenReturn(emptyList);

        incomeService.calculateAndSaveIncomeForCompletedFunds();

        verify(fundService, times(1)).findCompletedFundsNotProcessedWithCommission();
        verify(incomeRepository, never()).save(any(Income.class));
    }
}