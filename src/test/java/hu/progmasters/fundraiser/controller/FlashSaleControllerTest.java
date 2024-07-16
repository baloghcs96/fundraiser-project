package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.dto.incoming.FlashSaleItemSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.FlashSaleCreateInfo;
import hu.progmasters.fundraiser.dto.outgoing.FlashSaleItemInfo;
import hu.progmasters.fundraiser.service.AccountService;
import hu.progmasters.fundraiser.service.FlashSaleService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FlashSaleControllerTest {

    @Mock
    private FlashSaleService flashSaleService;

    @Mock
    private SecurityService securityService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private FlashSaleController flashSaleController;

    private AutoCloseable closeable;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(flashSaleController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }


    @Test
    void testSaveFlashSaleItem() throws Exception {
        FlashSaleCreateInfo expectedResponse = new FlashSaleCreateInfo();
        when(flashSaleService.saveFlashSaleItem(any(FlashSaleItemSaveUpdateCommand.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/fundraiser/flashSale/saveFlashSaleItem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());

        verify(flashSaleService, times(1)).saveFlashSaleItem(any(FlashSaleItemSaveUpdateCommand.class));
    }

    @Test
    void testGetFlashSaleItemByAccountIdAndGrade() throws Exception {
        List<FlashSaleItemInfo> expectedResponse = Collections.singletonList(new FlashSaleItemInfo());
        when(flashSaleService.getFlashSaleItemByAccountIdAndGrade(anyLong())).thenReturn(expectedResponse);

        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);

        mockMvc.perform(get("/api/fundraiser/flashSale/getFlashSaleItem"))
                .andExpect(status().isOk());

        verify(flashSaleService, times(1)).getFlashSaleItemByAccountIdAndGrade(anyLong());
    }

}