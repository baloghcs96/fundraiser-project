package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.dto.incoming.PurchaseCreateCommand;
import hu.progmasters.fundraiser.service.AccountService;
import hu.progmasters.fundraiser.service.PurchaseService;
import hu.progmasters.fundraiser.service.SecurityService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PurchaseControllerTest {

    @Mock
    private PurchaseService purchaseService;

    @Mock
    private SecurityService securityService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private PurchaseController purchaseController;

    private MockMvc mockMvc;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(purchaseController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testSavePurchase() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);

        mockMvc.perform(post("/api/fundraiser/purchase/savePurchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());

        verify(purchaseService, times(1)).savePurchase(any(PurchaseCreateCommand.class), anyLong());
    }

    @Test
    void testGetPurchase() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);

        mockMvc.perform(get("/api/fundraiser/purchase/myPurchase"))
                .andExpect(status().isOk());

        verify(purchaseService, times(1)).getPurchase(anyLong());
    }


}