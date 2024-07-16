package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.FlashSale;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.dto.incoming.FlashSaleItemSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.FlashSaleCreateInfo;
import hu.progmasters.fundraiser.dto.outgoing.FlashSaleItemInfo;
import hu.progmasters.fundraiser.exception.FlashSaleItemNotFoundException;
import hu.progmasters.fundraiser.repository.FlashSaleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FlashSaleServiceTest {

    @Mock
    private FlashSaleRepository flashSaleRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FlashSaleService flashSaleService;

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
    void testSaveFlashSaleItem() {
        FlashSaleItemSaveUpdateCommand command = new FlashSaleItemSaveUpdateCommand();
        FlashSale flashSale = new FlashSale();
        FlashSaleCreateInfo expected = new FlashSaleCreateInfo();
        command.setGrade(Grade.SILVER);

        when(modelMapper.map(command, FlashSale.class)).thenReturn(flashSale);
        when(flashSaleRepository.save(flashSale)).thenReturn(flashSale);
        when(modelMapper.map(flashSale, FlashSaleCreateInfo.class)).thenReturn(expected);

        FlashSaleCreateInfo result = flashSaleService.saveFlashSaleItem(command);

        assertEquals(expected, result);
        verify(flashSaleRepository, times(1)).save(flashSale);
    }

    @Test
    void testGetFlashSaleItemByAccountIdAndGrade() {
        Account account = new Account();
        account.setGrade(Grade.SILVER);
        FlashSale flashSale = new FlashSale();
        flashSale.setGrade(Grade.SILVER);
        flashSale.setPriceInCoin(100);
        flashSale.setQuantity(2);
        FlashSaleItemInfo expected = new FlashSaleItemInfo();
        expected.setPriceInCoin(50);

        when(accountService.findAccountById(1L)).thenReturn(account);
        when(flashSaleRepository.findAllByGrade(2)).thenReturn(Collections.singletonList(flashSale));
        when(modelMapper.map(flashSale, FlashSaleItemInfo.class)).thenReturn(expected);

        List<FlashSaleItemInfo> result = flashSaleService.getFlashSaleItemByAccountIdAndGrade(1L);

        assertEquals(1, result.size());
        assertEquals(expected, result.get(0));
    }

    @Test
    void testCalculatePrice() {
        Grade grade = Grade.GOLD;
        int coin = 100;
        int accountDiscount = grade.getDiscount();
        Grade flashSaleValue = Grade.SILVER;

        int expectedPrice = 90;
        int actualPrice = flashSaleService.calculatePrice(flashSaleValue.getValue(), coin, grade.getValue(), accountDiscount);

        assertEquals(expectedPrice, actualPrice, "The calculated price should apply a 10% discount.");
    }

    @Test
    void testFindItemByIdSuccess() {
        FlashSale expectedFlashSale = new FlashSale();
        expectedFlashSale.setItemId(1L);
        when(flashSaleRepository.findById(1L)).thenReturn(Optional.of(expectedFlashSale));

        FlashSale actualFlashSale = flashSaleService.findItemById(1L);

        assertEquals(expectedFlashSale, actualFlashSale);
    }


    @Test
    void testFindItemByIdNotFoundAndItemIdCheck() {
        long expectedItemId = 1L;
        when(flashSaleRepository.findById(expectedItemId)).thenReturn(Optional.empty());

        FlashSaleItemNotFoundException thrown = assertThrows(FlashSaleItemNotFoundException.class, () -> flashSaleService.findItemById(expectedItemId));
        assertEquals(expectedItemId, thrown.getItemId(), "The itemId in the exception should match the searched itemId.");
    }

}