package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.Exchange;
import hu.progmasters.fundraiser.domain.entity.Fund;
import hu.progmasters.fundraiser.domain.entity.Transaction;
import hu.progmasters.fundraiser.domain.entity.token.TransactionVerificationToken;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.domain.enumeration.TransactionState;
import hu.progmasters.fundraiser.dto.incoming.TransactionFilterCommand;
import hu.progmasters.fundraiser.dto.incoming.TransactionSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.DonatorInfo;
import hu.progmasters.fundraiser.dto.outgoing.FilteredTransactionInfo;
import hu.progmasters.fundraiser.dto.outgoing.TransactionAccountInfo;
import hu.progmasters.fundraiser.dto.outgoing.TransactionFundInfo;
import hu.progmasters.fundraiser.exception.InvalidScheduleTimeException;
import hu.progmasters.fundraiser.exception.NotEnoughBalanceToTransferException;
import hu.progmasters.fundraiser.exception.TransactionToYourselfException;
import hu.progmasters.fundraiser.repository.TransactionRepository;
import hu.progmasters.fundraiser.service.token.TransactionVerificationTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private FundService fundService;

    @Mock
    private ExchangeService exchangeService;

    @Mock
    private TransactionVerificationTokenService verificationTokenService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AutoCloseable closeable;

    @InjectMocks
    private TransactionService transactionService;

    private Account account1;
    private Account account2;
    private Account account3;
    private Fund fund1;
    private Exchange exchange;
    private Transaction transaction1;
    private Transaction transaction2;


    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        account1 = new Account();
        account1.setAccountId(1L);
        account1.setBalance(100.0);
        account1.setCurrency(Currency.HUF);
        account1.setGrade(Grade.BRONZE);

        account2 = new Account();
        account2.setAccountId(2L);
        account2.setBalance(200.0);
        account2.setCurrency(Currency.EUR);
        account2.setGrade(Grade.BRONZE);

        account3 = new Account();
        account3.setAccountId(3L);
        account3.setBalance(300.0);
        account3.setCurrency(Currency.USD);
        account3.setGrade(Grade.BRONZE);

        fund1 = new Fund();
        fund1.setTitle("Fund1");
        fund1.setFundId(1L);
        fund1.setAccount(account2);
        fund1.setCurrency(Currency.EUR);
        fund1.setGoalAmount(1000.0);
        fund1.setCurrentAmount(0.0);

        transaction1 = new Transaction();
        transaction1.setTransactionId(1L);
        transaction1.setSentAmount(100.0);
        transaction1.setSenderAccount(account3);
        transaction1.setTargetFund(fund1);

        transaction2 = new Transaction();
        transaction2.setTransactionId(2L);
        transaction2.setSentAmount(200.0);
        transaction2.setSenderAccount(account3);
        transaction2.setTargetFund(fund1);

        exchange = new Exchange();
        exchange.setId(1L);
        exchange.setBaseCurrency("EUR");
        exchange.setHufCurrency(385.0);
        exchange.setUsdCurrency(1.05);

    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testSaveTransaction() {
        Account senderAccount = account1;
        Fund targetFund = fund1;

        TransactionSaveUpdateCommand command = new TransactionSaveUpdateCommand(senderAccount.getAccountId(),
                100.0, LocalDateTime.now().plusHours(1));
        Transaction transaction = new Transaction();
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
        when(modelMapper.map(any(TransactionSaveUpdateCommand.class), eq(Transaction.class))).thenReturn(transaction);
        when(accountService.findAccountById(senderAccount.getAccountId())).thenReturn(senderAccount);
        when(fundService.findFundById(targetFund.getFundId())).thenReturn(targetFund);

        transactionService.saveTransaction(command, senderAccount.getAccountId(), request);

        verify(transactionRepository, times(1)).save(transaction);
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void testSaveTransaction_NotEnoughBalanceToTransferException() {
        Account senderAccount = account1;
        Fund targetFund = fund1;

        TransactionSaveUpdateCommand command = new TransactionSaveUpdateCommand(targetFund.getFundId(),
                1500.0, LocalDateTime.now().plusHours(1));
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
        when(modelMapper.map(any(TransactionSaveUpdateCommand.class), eq(Transaction.class))).thenReturn(new Transaction());
        when(accountService.findAccountById(senderAccount.getAccountId())).thenReturn(senderAccount);
        when(fundService.findFundById(targetFund.getFundId())).thenReturn(targetFund);

        assertThrows(NotEnoughBalanceToTransferException.class, () ->
                transactionService.saveTransaction(command, senderAccount.getAccountId(), request));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testSaveTransaction_InvalidScheduledTimeException() {
        Account senderAccount = account1;
        Fund targetFund = fund1;
        TransactionSaveUpdateCommand command = new TransactionSaveUpdateCommand(fund1.getFundId(),
                50.0, LocalDateTime.now().minusHours(1));
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
        when(modelMapper.map(any(TransactionSaveUpdateCommand.class), eq(Transaction.class))).thenReturn(new Transaction());
        when(accountService.findAccountById(senderAccount.getAccountId())).thenReturn(senderAccount);
        when(fundService.findFundById(targetFund.getFundId())).thenReturn(targetFund);

        assertThrows(InvalidScheduleTimeException.class, () ->
                transactionService.saveTransaction(command, senderAccount.getAccountId(), request));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }


    @Test
    void testSaveTransaction_TransactionToYourselfException() {
        Account senderAccount = account2;
        Fund targetFund = fund1;
        TransactionSaveUpdateCommand command = new TransactionSaveUpdateCommand(fund1.getFundId(),
                50.0, LocalDateTime.now().minusHours(1));
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
        when(modelMapper.map(any(TransactionSaveUpdateCommand.class), eq(Transaction.class))).thenReturn(new Transaction());
        when(accountService.findAccountById(senderAccount.getAccountId())).thenReturn(senderAccount);
        when(fundService.findFundById(targetFund.getFundId())).thenReturn(targetFund);

        assertThrows(TransactionToYourselfException.class, () ->
                transactionService.saveTransaction(command, senderAccount.getAccountId(), request));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }


    @Test
    void testVerificationTransfer() {
        String token = "test-token";
        TransactionVerificationToken verificationToken = new TransactionVerificationToken(1L);
        verificationToken.setToken(token);
        Transaction transaction = new Transaction();
        transaction.setSenderAccount(account1);
        transaction.setTargetFund(fund1);
        transaction.setTransactionId(1L);
        transaction.setTransactionState(TransactionState.NOT_VERIFIED);

        when(verificationTokenService.getTransactionVerificationTokenByToken(token)).thenReturn(verificationToken);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.verificationTransfer(token);

        verify(verificationTokenService, times(1)).getTransactionVerificationTokenByToken(token);
        verify(transactionRepository, times(1)).findById(1L);
        verify(verificationTokenService, times(1)).deleteTransactionToken(verificationToken);
        assertEquals(TransactionState.COMPLETED, transaction.getTransactionState());
    }

    @Test
    void testGetDonatorInfosByFundId() {
        Transaction transaction = new Transaction();
        Account account = new Account();
        account.setAccountId(1L);
        transaction.setSenderAccount(account);
        transaction.setSentAmount(100.0);

        when(transactionRepository.findAllByFundId(anyLong())).thenReturn(List.of(transaction));

        List<DonatorInfo> result = transactionService.getDonatorInfosByFundId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionInfosByAccountId() {
        Transaction transaction = new Transaction();
        when(transactionRepository.findAllBySenderAccountId(anyLong())).thenReturn(List.of(transaction));
        when(modelMapper.map(any(Transaction.class), any())).thenReturn(new TransactionAccountInfo());

        List<TransactionAccountInfo> result = transactionService.getTransactionInfosByAccountId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionsInfosByFundId() {
        Transaction transaction = new Transaction();
        when(transactionRepository.findAllByFundId(anyLong())).thenReturn(List.of(transaction));
        when(modelMapper.map(any(Transaction.class), any())).thenReturn(new TransactionFundInfo());

        List<TransactionFundInfo> result = transactionService.getTransactionsInfosByFundId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredTransactionInfosByAccountId() {
        Account account = account3;

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        TransactionFilterCommand filterCommand = new TransactionFilterCommand();
        filterCommand.setSentAmount(150.0);

        FilteredTransactionInfo filteredInfo = new FilteredTransactionInfo();
        filteredInfo.setSentAmount(200.0);

        when(accountService.findAccountById(account.getAccountId())).thenReturn(account);
        when(transactionRepository.findAllBySenderAccountId(account.getAccountId())).thenReturn(transactions);
        when(modelMapper.map(any(Transaction.class), any())).thenReturn(filteredInfo);

        List<FilteredTransactionInfo> result = transactionService.getFilteredTransactionInfosByAccountId
                (account.getAccountId(), filterCommand);

        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).getSentAmount());
    }

    @Test
    void testExceptionInstantiationAndBalanceRetrieval() {
        double expectedBalance = 50.0;
        NotEnoughBalanceToTransferException exception = new NotEnoughBalanceToTransferException(expectedBalance);

        assertNotNull(exception);
        assertEquals(expectedBalance, exception.getBalance(), "The balance returned by getBalance() should match the value passed to the constructor.");
    }

    @Test
    void testExceptionInstantiationAndDataRetrieval() {
        Long expectedAccountId = 1L;
        Long expectedFundId = 2L;
        TransactionToYourselfException exception = new TransactionToYourselfException(expectedAccountId, expectedFundId);

        assertNotNull(exception, "Exception should be instantiated.");
        assertEquals(expectedAccountId, exception.getAccountId(), "The accountId returned by getAccountId() should match the value passed to the constructor.");
        assertEquals(expectedFundId, exception.getFundId(), "The fundId returned by getFundId() should match the value passed to the constructor.");
    }


//    @Test
//    void scheduleTransactionTest() {
//
//    }
}