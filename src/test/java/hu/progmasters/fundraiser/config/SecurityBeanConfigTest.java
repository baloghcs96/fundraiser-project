package hu.progmasters.fundraiser.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityBeanConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void passwordEncoderBeanShouldNotBeNull() {
        assertNotNull(passwordEncoder);
    }

}