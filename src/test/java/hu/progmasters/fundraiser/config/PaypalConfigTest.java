package hu.progmasters.fundraiser.config;

import com.paypal.base.rest.APIContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
        "appConfig.paypal.client-id=clientId123",
        "appConfig.paypal.client-secret=secret123",
        "appConfig.paypal.mode=sandbox",
        "appConfig.payment.cancel-url=http://example.com/cancel",
        "appConfig.payment.success-url=http://example.com/success"
})
class PaypalConfigTest {

    @Autowired
    private APIContext apiContext;

    @Autowired
    private PaypalConfig paypalConfig;

    @Test
    public void apiContextBeanShouldNotBeNull() {
        assertNotNull(apiContext);
    }

    @Test
    public void cancelUrlShouldBeCorrect() {
        assertEquals("http://example.com/cancel", paypalConfig.getCancelUrl());
    }

    @Test
    public void successUrlShouldBeCorrect() {
        assertEquals("http://example.com/success", paypalConfig.getSuccessUrl());
    }
}