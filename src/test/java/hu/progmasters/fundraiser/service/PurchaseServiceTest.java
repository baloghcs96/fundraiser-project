package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.FlashSale;
import hu.progmasters.fundraiser.domain.entity.Purchase;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.dto.incoming.PurchaseCreateCommand;
import hu.progmasters.fundraiser.exception.NotEnoughCoinToPurchaseException;
import hu.progmasters.fundraiser.repository.PurchaseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private FlashSaleService flashSaleService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PurchaseService purchaseService;

    private Account account1;

    private FlashSale flashSale1;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        account1 = new Account();
        account1.setAccountId(1L);
        account1.setBalance(200.0);
        account1.setCurrency(Currency.HUF);
        account1.setGrade(Grade.BRONZE);
        account1.setCoin(200);

        flashSale1 = new FlashSale();
        flashSale1.setItemId(1L);
        flashSale1.setQuantity(2);
        flashSale1.setGrade(Grade.SILVER);
        flashSale1.setPriceInCoin(100);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testSavePurchase() {
        PurchaseCreateCommand command = new PurchaseCreateCommand();
        command.setItemId(flashSale1.getItemId());
        command.setQuantity(1);

        Purchase purchase = new Purchase();
        purchase.setAccount(account1);
        purchase.setItem(flashSale1.getItem());
        purchase.setPriceInCoin(100);
        purchase.setQuantity(1);

        when(modelMapper.map(command, Purchase.class)).thenReturn(purchase);

        when(accountService.findAccountById(account1.getAccountId())).thenReturn(account1);
        when(flashSaleService.findItemById(command.getItemId())).thenReturn(flashSale1);
        int calculatedPrice = flashSaleService.calculatePrice(flashSale1.getGrade().getValue(), flashSale1.getPriceInCoin(),
                account1.getGrade().getValue(), account1.getGrade().getDiscount());
        when(flashSaleService.calculatePrice(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(calculatedPrice);

        purchaseService.savePurchase(command, account1.getAccountId());

        verify(purchaseRepository, times(1)).save(any());
    }

    @Test
    void testGetPurchase() {
        when(accountService.findAccountById(account1.getAccountId())).thenReturn(account1);
        when(purchaseRepository.findPurchasesByAccountId(account1.getAccountId())).thenReturn(Collections.emptyList());

        purchaseService.getPurchase(account1.getAccountId());

        verify(purchaseRepository, times(1)).findPurchasesByAccountId(account1.getAccountId());
    }

    @Test
    void testExceptionInstantiationAndCoinRetrieval() {
        int expectedCoin = 100;
        NotEnoughCoinToPurchaseException exception = new NotEnoughCoinToPurchaseException(expectedCoin);

        assertNotNull(exception, "Exception should be instantiated.");
        assertEquals(expectedCoin, exception.getAccountCoin(), "The coin value returned by getAccountCoin() should match the value passed to the constructor.");
    }


}