package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.Badge;
import hu.progmasters.fundraiser.domain.entity.token.AccountVerificationToken;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.dto.incoming.AccountSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.AccountInfo;
import hu.progmasters.fundraiser.exception.*;
import hu.progmasters.fundraiser.repository.AccountRepository;
import hu.progmasters.fundraiser.service.token.AccountVerificationTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    AccountVerificationTokenService accountVerificationTokenService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private BadgeService badgeService;
    @InjectMocks
    private AccountService accountService;

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
    void testSaveAccountWithExistingEmail() {
        AccountSaveUpdateCommand command = new AccountSaveUpdateCommand();
        command.setAccountName("testUser");
        command.setEmail("existing@example.com");

        when(accountRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(AccountAlreadyExistByEmailException.class, () ->
                accountService.saveAccount(command, mock(HttpServletRequest.class)));
    }


    @Test
    void testSaveAccountWithExistingUsername() {
        AccountSaveUpdateCommand command = new AccountSaveUpdateCommand();
        command.setAccountName("existingUser");
        command.setEmail("test@example.com");

        when(accountRepository.findByAccountName(anyString())).thenReturn(Optional.of(new Account()));

        assertThrows(AccountAlreadyExistByNameException.class, () -> accountService.saveAccount(command, mock(HttpServletRequest.class)));
    }

    @Test
    void testSaveAccountSuccess() {
        AccountSaveUpdateCommand command = new AccountSaveUpdateCommand();
        command.setAccountName("newUser");
        command.setEmail("new@example.com");
        command.setPassword("password");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/test"));

        Account mockAccount = new Account();
        Badge mockBadgeInfo = new Badge();
        mockBadgeInfo.setImageUrl("mockImageUrl");

        when(accountRepository.existsByEmail(anyString())).thenReturn(false);
        when(accountRepository.findByAccountName(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(modelMapper.map(any(AccountSaveUpdateCommand.class), eq(Account.class))).thenReturn(mockAccount);
        when(modelMapper.map(any(Account.class), eq(AccountInfo.class))).thenReturn(new AccountInfo());
        when(badgeService.getBadgeByGrade(any(Grade.class))).thenReturn(mockBadgeInfo);

        AccountInfo result = accountService.saveAccount(command, mockRequest);

        assertNotNull(result);
        verify(accountRepository, times(1)).save(any(Account.class));
        assertEquals("mockImageUrl", result.getBadge());
    }

    @Test
    void testFindAccountByNameFound() {
        String name = "testUser";
        Account expectedAccount = new Account();
        expectedAccount.setAccountName(name);
        when(accountRepository.findByAccountName(name)).thenReturn(Optional.of(expectedAccount));

        Account result = accountService.findAccountByName(name);

        assertNotNull(result);
        assertEquals(name, result.getAccountName());
    }

    @Test
    void testFindAccountByNameNotFound() {
        String name = "nonExistingUser";
        when(accountRepository.findByAccountName(name)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundByNameException.class, () -> accountService.findAccountByName(name));
    }

    @Test
    void testConfirmRegistrationSuccess() {
        String token = "validToken";
        Long accountId = 1L;
        AccountVerificationToken accountVerificationToken = new AccountVerificationToken();
        accountVerificationToken.setAccountId(accountId);
        Account account = new Account();
        account.setAccountId(accountId);
        account.setVerified(false);

        when(accountVerificationTokenService.getAccountVerificationTokenByToken(token)).thenReturn(accountVerificationToken);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doNothing().when(accountVerificationTokenService).isAccountTokenExpired(any(AccountVerificationToken.class));
        doNothing().when(accountVerificationTokenService).deleteAccountToken(any(AccountVerificationToken.class));

        accountService.confirmRegistration(token);

        assertTrue(account.isVerified());
        verify(accountVerificationTokenService, times(1)).deleteAccountToken(accountVerificationToken);
    }

    @Test
    void testConfirmRegistrationTokenExpired() {
        String token = "expiredToken";
        when(accountVerificationTokenService.getAccountVerificationTokenByToken(token)).thenThrow(new AccountVerificationTokenNotFoundByTokenException(token));

        assertThrows(AccountVerificationTokenNotFoundByTokenException.class, () -> accountService.confirmRegistration(token));
    }

    @Test
    void testUpdateAccountNotFound() {
        Long accountId = 2L;
        AccountSaveUpdateCommand command = new AccountSaveUpdateCommand();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundByIdException.class, () -> accountService.updateAccount(accountId, command, mock(HttpServletRequest.class)));
    }


}