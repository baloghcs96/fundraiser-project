package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class JpaUserDetailsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private JpaUserDetailsService jpaUserDetailsService;

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
    void loadUserByUsername_whenUserExists() {
        String accountName = "testUser";
        Account account = new Account();
        account.setAccountName(accountName);
        account.setPassword("password");
        account.setAuthorities("ROLE_USER");

        when(accountRepository.findByAccountName(accountName)).thenReturn(Optional.of(account));

        UserDetails userDetails = jpaUserDetailsService.loadUserByUsername(accountName);

        assertThat(userDetails.getUsername()).isEqualTo(accountName);
        assertThat(userDetails.getPassword()).isEqualTo(account.getPassword());
        assertThat(userDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist() {
        String accountName = "nonExistentUser";
        when(accountRepository.findByAccountName(accountName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jpaUserDetailsService.loadUserByUsername(accountName))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("username not found");
    }
}