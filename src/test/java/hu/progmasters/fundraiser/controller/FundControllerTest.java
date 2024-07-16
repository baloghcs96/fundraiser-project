package hu.progmasters.fundraiser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.progmasters.fundraiser.domain.enumeration.Category;
import hu.progmasters.fundraiser.dto.incoming.FundSaveCommand;
import hu.progmasters.fundraiser.dto.outgoing.FundInfo;
import hu.progmasters.fundraiser.service.AccountService;
import hu.progmasters.fundraiser.service.FundService;
import hu.progmasters.fundraiser.service.SecurityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FundControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private FundService fundService;
    @Mock
    private SecurityService securityService;
    @Mock
    private AccountService accountService;
    @InjectMocks
    private FundController fundController;
    private MockMvc mockMvc;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fundController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    /*
        @Test
        void saveFund() throws Exception {
            Authentication authentication = mock(Authentication.class);
            when(securityService.getAuthentication()).thenReturn(authentication);
            when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);
            FundSaveCommand command = new FundSaveCommand();
            FundInfo expectedResponse = new FundInfo();
            when(fundService.saveFund(any(FundSaveCommand.class), anyLong())).thenReturn(expectedResponse);
            MockMultipartFile firstFile = new MockMultipartFile("images", "image1.jpg", "image/jpeg", "some-image".getBytes());
            MockMultipartFile secondFile = new MockMultipartFile("images", "image2.jpg", "image/jpeg", "some-image".getBytes());
            mockMvc.perform(multipart("/api/fundraiser/fund")
                            .file(firstFile)
                            .file(secondFile)
                            .param("name", "Valid Name")
                            .param("amount", "1000")
                            .param("currency", "HUF")
                            .param("description", "Valid Description")
                            .param("goalAmount", "10000")
                            .param("title", "Valid Title")
                            .param("category", "ART") // Assuming ART is a valid enum value
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            }))
                    .andExpect(status().isCreated());
            verify(fundService, times(1)).saveFund(any(FundSaveCommand.class), anyLong());
        }
    */
    @Test
    void saveFund_withValidData_returnsCreated() throws Exception {
        FundSaveCommand command = new FundSaveCommand();
        command.setTitle("Valid Name");
        command.setCurrency("HUF");
        command.setDescription("Valid Description");
        command.setGoalAmount(10000.0);
        command.setCategory(String.valueOf(Category.ART));

        FundInfo expectedResponse = new FundInfo();
        expectedResponse.setFundId(1L);

        when(securityService.getAuthentication()).thenReturn(mock(Authentication.class));
        when(fundService.saveFund(any(FundSaveCommand.class), any(Authentication.class))).thenReturn(expectedResponse);

        mockMvc.perform(multipart("/api/fundraiser/fund")
                        .file(new MockMultipartFile("images", "image1.jpg", "image/jpeg", "some-image".getBytes()))
                        .file(new MockMultipartFile("images", "image2.jpg", "image/jpeg", "some-image".getBytes()))
                        .param("title", "Valid Name")
                        .param("currency", "HUF")
                        .param("description", "Valid Description")
                        .param("goalAmount", "10000")
                        .param("category", "ART"))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(fundService, times(1)).saveFund(any(FundSaveCommand.class), any(Authentication.class));
    }

    @Test
    void updateFund() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);
        MockMultipartFile firstFile = new MockMultipartFile("images", "image1.jpg", "image/jpeg", "some-image".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("images", "image2.jpg", "image/jpeg", "some-image".getBytes());
        mockMvc.perform(multipart("/api/fundraiser/fund/update/1")
                        .file(firstFile)
                        .file(secondFile)
                        .param("name", "Updated Name")
                        .param("amount", "2000")
                        .param("currency", "USD")
                        .param("description", "Updated Description")
                        .param("goalAmount", "20000")
                        .param("title", "Updated Title")
                        .param("category", "MUSIC") // Assuming MUSIC is a valid enum value
                        .with(request -> {
                            request.setMethod("PUT"); // Use POST for multipart/form-data
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    void saveFund_withInvalidData_returnsBadRequest() throws Exception {
        FundSaveCommand command = new FundSaveCommand();
        // Assuming validation annotations in FundSaveCommand are present and this setup violates them

        mockMvc.perform(post("/api/fundraiser/fund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());

        verify(fundService, never()).saveFund(any(FundSaveCommand.class), any(Authentication.class));
    }

    @Test
    void getFundById() throws Exception {
        FundInfo expectedResponse = new FundInfo();
        when(fundService.getFundInfoById(1L)).thenReturn(expectedResponse);
        mockMvc.perform(get("/api/fundraiser/fund/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
        verify(fundService, times(1)).getFundInfoById(1L);
    }

    @Test
    void findAllByCategory() throws Exception {
        List<FundInfo> expectedResponse = Arrays.asList(new FundInfo(), new FundInfo());
        when(fundService.findAllByCategory(Category.ART)).thenReturn(expectedResponse);
        mockMvc.perform(get("/api/fundraiser/fund/category")
                        .param("category", "ART"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
        verify(fundService, times(1)).findAllByCategory(Category.ART);
    }

    @Test
    void findAllByTitle() throws Exception {
        List<FundInfo> expectedResponse = Arrays.asList(new FundInfo(), new FundInfo());
        when(fundService.findAllByTitle("Sample Title")).thenReturn(expectedResponse);
        mockMvc.perform(get("/api/fundraiser/fund/title")
                        .param("title", "Sample Title"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
        verify(fundService, times(1)).findAllByTitle("Sample Title");
    }

}