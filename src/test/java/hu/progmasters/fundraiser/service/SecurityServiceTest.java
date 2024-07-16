package hu.progmasters.fundraiser.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

class SecurityServiceTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private SecurityService securityService;

    private AutoCloseable closeable;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        securityService = new SecurityService(authenticationManager);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void getAuthentication_whenAuthenticationExists_shouldReturnAuthentication() {

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        Authentication result = securityService.getAuthentication();

        assertSame(authentication, result, "The returned authentication should be the same as the mocked one.");
    }

}