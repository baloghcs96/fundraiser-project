package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.dto.incoming.TransactionFilterCommand;
import hu.progmasters.fundraiser.dto.incoming.TransactionSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.DonatorInfo;
import hu.progmasters.fundraiser.dto.outgoing.FilteredTransactionInfo;
import hu.progmasters.fundraiser.dto.outgoing.TransactionAccountInfo;
import hu.progmasters.fundraiser.dto.outgoing.TransactionFundInfo;
import hu.progmasters.fundraiser.service.AccountService;
import hu.progmasters.fundraiser.service.SecurityService;
import hu.progmasters.fundraiser.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private SecurityService securityService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testSaveTransaction() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);

        mockMvc.perform(post("/api/fundraiser/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fundId\": 1, \"sentAmount\": 100.0, \"transactionTime\": \"2024-07-03T10:00:00\"}"))
                .andExpect(status().isCreated());

        verify(transactionService, times(1)).saveTransaction(any(TransactionSaveUpdateCommand.class), anyLong(), any(HttpServletRequest.class));
    }

    @Test
    void testVerificationTransfer() throws Exception {
        mockMvc.perform(get("/api/fundraiser/transaction/verificationConfirm")
                        .param("token", "sampleToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction verified!"));

        verify(transactionService, times(1)).verificationTransfer("sampleToken");
    }

    @Test
    void testGetTransactionsInfosByFundId() throws Exception {
        List<TransactionFundInfo> transactionFundInfos = Collections.singletonList(new TransactionFundInfo());
        when(transactionService.getTransactionsInfosByFundId(anyLong())).thenReturn(transactionFundInfos);

        mockMvc.perform(get("/api/fundraiser/transaction/fund/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(transactionService, times(1)).getTransactionsInfosByFundId(1L);
    }

    @Test
    void testGetDonatorInfosByFundId() throws Exception {
        List<DonatorInfo> donatorInfos = Collections.singletonList(new DonatorInfo());
        when(transactionService.getDonatorInfosByFundId(anyLong())).thenReturn(donatorInfos);

        mockMvc.perform(get("/api/fundraiser/transaction/fund/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(transactionService, times(1)).getDonatorInfosByFundId(1L);
    }

    @Test
    void testGetFilteredTransactionsByAccountId() throws Exception {
        List<FilteredTransactionInfo> filteredTransactionInfos = Collections.singletonList(new FilteredTransactionInfo());
        when(transactionService.getFilteredTransactionInfosByAccountId(anyLong(), any(TransactionFilterCommand.class))).thenReturn(filteredTransactionInfos);

        mockMvc.perform(get("/api/fundraiser/transaction/account/filter/1")
                        .param("fundTitle", "title")
                        .param("minSentAmount", "100.0")
                        .param("transactionDate", "2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(transactionService, times(1)).getFilteredTransactionInfosByAccountId(anyLong(), any(TransactionFilterCommand.class));
    }

    @Test
    void testGetTransactionInfosByAccountId() throws Exception {
        List<TransactionAccountInfo> transactionAccountInfos = Collections.singletonList(new TransactionAccountInfo());
        when(transactionService.getTransactionInfosByAccountId(anyLong())).thenReturn(transactionAccountInfos);

        mockMvc.perform(get("/api/fundraiser/transaction/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(transactionService, times(1)).getTransactionInfosByAccountId(1L);
    }
}