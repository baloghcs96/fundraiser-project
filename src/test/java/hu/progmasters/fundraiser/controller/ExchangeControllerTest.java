package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.dto.outgoing.ExchangeInfo;
import hu.progmasters.fundraiser.service.ExchangeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExchangeControllerTest {

    @Mock
    private ExchangeService exchangeService;

    @InjectMocks
    private ExchangeController exchangeController;

    private MockMvc mockMvc;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(exchangeController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testGetAndSaveLatestRates() throws Exception {
        ExchangeInfo expectedResponse = new ExchangeInfo();
        when(exchangeService.getAndSaveLatestRates()).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/fundraiser/exchange/latest"))
                .andExpect(status().isOk());

        verify(exchangeService, times(1)).getAndSaveLatestRates();
    }

}