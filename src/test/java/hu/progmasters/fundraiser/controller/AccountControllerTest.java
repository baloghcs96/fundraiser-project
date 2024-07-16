package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.dto.incoming.AccountChangePassword;
import hu.progmasters.fundraiser.dto.incoming.AccountGetEmailForForgottenPasswordCommand;
import hu.progmasters.fundraiser.dto.incoming.AccountSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.AccountInfo;
import hu.progmasters.fundraiser.dto.outgoing.ChooseCurrency;
import hu.progmasters.fundraiser.service.AccountService;
import hu.progmasters.fundraiser.service.SecurityService;
import hu.progmasters.fundraiser.service.WebService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private SecurityService securityService;

    @Mock
    private WebService webService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testSaveAccount() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);

        AccountInfo expectedResponse = new AccountInfo(1L, "Valid Name", "email@example.com", Currency.HUF, Grade.BRONZE, 0.0, false, "BRONZE");

        when(accountService.saveAccount(any(AccountSaveUpdateCommand.class), any(HttpServletRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/fundraiser/account/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"accountName\": \"Valid Name\",\n" +
                                "  \"password\": \"Password-123\",\n" +
                                "  \"email\": \"email@example.com\",\n" +
                                "  \"currency\": \"HUF\"\n" +
                                "}")
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                )
                .andExpect(status().isCreated());

        verify(accountService, times(1)).saveAccount(any(AccountSaveUpdateCommand.class), any(HttpServletRequest.class));
    }

    @Test
    void confirmRegistration() throws Exception {

        mockMvc.perform(get("/api/fundraiser/account/save/registrationConfirm")
                        .param("token", "sampleToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration confirmed successfully!"));

        verify(accountService, times(1)).confirmRegistration("sampleToken");
    }

    @Test
    void logout() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);

        mockMvc.perform(post("/api/fundraiser/account/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));

        verify(accountService, times(1)).logout(any(HttpServletRequest.class), any(HttpServletResponse.class), eq(authentication));
    }

    @Test
    void getAvailableCurrenciesForNewAccount() throws Exception {
        ChooseCurrency chooseCurrency = new ChooseCurrency(Collections.singletonList("HUF"));
        when(accountService.getAvailableCurrenciesForNewAccount()).thenReturn(chooseCurrency);

        mockMvc.perform(get("/api/fundraiser/account/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").isArray())
                .andExpect(jsonPath("$.currency[0]").value("HUF"));

        verify(accountService, times(1)).getAvailableCurrenciesForNewAccount();
    }

    @Test
    void getMyAccount() throws Exception {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        AccountInfo expectedAccountInfo = new AccountInfo(1L, "User Name", "user@example.com", Currency.HUF, Grade.BRONZE, 1000.0, false, "BRONZE");

        when(securityService.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);
        when(accountService.getAccountInfo(1L)).thenReturn(expectedAccountInfo);

        mockMvc.perform(get("/api/fundraiser/account/myAccount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(expectedAccountInfo.getAccountId()))
                .andExpect(jsonPath("$.accountName").value(expectedAccountInfo.getAccountName()))
                .andExpect(jsonPath("$.email").value(expectedAccountInfo.getEmail()))
                .andExpect(jsonPath("$.currency").value(expectedAccountInfo.getCurrency().toString()))
                .andExpect(jsonPath("$.grade").value(expectedAccountInfo.getGrade().toString()))
                .andExpect(jsonPath("$.balance").value(expectedAccountInfo.getBalance()))
                .andExpect(jsonPath("$.verified").value(expectedAccountInfo.isVerified()))
                .andExpect(jsonPath("$.badge").value(expectedAccountInfo.getBadge()));

        verify(accountService, times(1)).getAccountInfo(1L);
    }

    @Test
    void getAdminString() throws Exception {
        mockMvc.perform(get("/api/fundraiser/account/checkAdminRole"))
                .andExpect(status().isOk())
                .andExpect(content().string("You are an admin!"));
    }

    @Test
    void getUserString() throws Exception {
        mockMvc.perform(get("/api/fundraiser/account/checkUserRole"))
                .andExpect(status().isOk())
                .andExpect(content().string("You are an user!"));
    }

    @Test
    void createNewToken() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);

        mockMvc.perform(post("/api/fundraiser/account/createNewToken"))
                .andExpect(status().isCreated());

        verify(accountService, times(1)).createNewAccountToken(eq(authentication), any(HttpServletRequest.class));
    }

    @Test
    void updateAccount() throws Exception {
        AccountSaveUpdateCommand command = new AccountSaveUpdateCommand();
        command.setAccountName("Updated Name");
        command.setEmail("updated@example.com");
        command.setPassword("NewPassword-123");
        command.setCurrency("USD");

        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);
        when(accountService.getLoggedInUserId(authentication)).thenReturn(1L);

        AccountInfo expectedResponse = new AccountInfo(1L, "Updated Name", "updated@example.com", Currency.USD, Grade.SILVER, 100.0, true, "SILVER");
        when(accountService.updateAccount(eq(1L), any(AccountSaveUpdateCommand.class), any(HttpServletRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/fundraiser/account/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"accountName\": \"Updated Name\",\n" +
                                "  \"password\": \"NewPassword-123\",\n" +
                                "  \"email\": \"updated@example.com\",\n" +
                                "  \"currency\": \"USD\"\n" +
                                "}"))
                .andExpect(status().isOk());

        verify(accountService, times(1)).updateAccount(eq(1L), any(AccountSaveUpdateCommand.class), any(HttpServletRequest.class));
    }

    @Test
    void resetPassword() throws Exception {
        AccountChangePassword command = new AccountChangePassword();
        command.setNewPassword("NewPassword-123");

        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);

        mockMvc.perform(put("/api/fundraiser/account/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"newPassword\": \"NewPassword-123\"\n" +
                                "}"))
                .andExpect(status().isOk());

        verify(accountService, times(1)).resetPassword(eq(authentication), any(AccountChangePassword.class));
    }

    @Test
    void resetForgottenPassword() throws Exception {
        AccountGetEmailForForgottenPasswordCommand command = new AccountGetEmailForForgottenPasswordCommand();
        command.setEmail("user@example.com");

        mockMvc.perform(put("/api/fundraiser/account/resetForgottenPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"user@example.com\"\n" +
                                "}"))
                .andExpect(status().isOk());

        verify(accountService, times(1)).resetForgottenPassword(any(HttpServletRequest.class), eq(command));
    }

    @Test
    void resetForgottenPasswordToken_successfullyResetsPassword() throws Exception {
        mockMvc.perform(post("/api/fundraiser/account/resetForgottenPassword/confirm")
                        .param("token", "validToken")
                        .param("newPassword", "NewSecurePassword123!"))
                .andExpect(status().isOk());
        //.andExpect(content().string(containsString("Your password has been reset successfully")));

        verify(accountService, times(1)).resetForgottenPasswordToken("validToken", "NewSecurePassword123!");
        verify(webService, times(1)).createHtmlContent("resetPasswordComplete", "Password reset completed - FUNDRAISER");
    }

    @Test
    void getLoggedInUserTest() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(securityService.getAuthentication()).thenReturn(authentication);
        mockMvc.perform(get("/api/fundraiser/account/login"))
                .andExpect(status().isOk());
        verify(securityService, times(1)).getAuthentication();
    }

    @Test
    void resetForgottenPasswordNewPasswordTest() throws Exception {
        String token = "validToken";
        doNothing().when(accountService).resetForgottenPasswordNewPassword(token);
        ResponseEntity<String> mockResponseEntity = ResponseEntity.ok("Mock HTML content");
        when(webService.createHtmlContent("resetForgottenPassword", "Reset Password - FUNDRAISER")).thenReturn(mockResponseEntity);
        mockMvc.perform(get("/api/fundraiser/account/resetForgottenPassword/newPassword")
                        .param("token", token))
                .andExpect(status().isOk());
        verify(accountService, times(1)).resetForgottenPasswordNewPassword(token);
        verify(webService, times(1)).createHtmlContent("resetForgottenPassword", "Reset Password - FUNDRAISER");
    }
}