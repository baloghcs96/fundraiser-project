package hu.progmasters.fundraiser.service.token;

import hu.progmasters.fundraiser.domain.entity.token.AccountVerificationToken;
import hu.progmasters.fundraiser.repository.token.AccountVerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class AccountVerificationTokenServiceTest {


    @Mock
    private AccountVerificationTokenRepository accountVerificationTokenRepository;

    @InjectMocks
    private AccountVerificationTokenService accountVerificationTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create new token if no existing token found for account")
    void shouldCreateNewTokenIfNoExistingTokenFound() {
        Long accountId = 1L;
        when(accountVerificationTokenRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        accountVerificationTokenService.createAccountVerificationToken(accountId);

        verify(accountVerificationTokenRepository, times(1)).save(any(AccountVerificationToken.class));
    }

    @Test
    @DisplayName("Should delete existing token and create new one for account")
    void shouldDeleteExistingTokenAndCreateNewOneForAccount() {
        Long accountId = 1L;
        AccountVerificationToken existingToken = new AccountVerificationToken(accountId);
        when(accountVerificationTokenRepository.findByAccountId(accountId)).thenReturn(Optional.of(existingToken));

        accountVerificationTokenService.createAccountVerificationToken(accountId);

        verify(accountVerificationTokenRepository, times(1)).delete(existingToken);
        verify(accountVerificationTokenRepository, times(1)).save(any(AccountVerificationToken.class));
    }

}