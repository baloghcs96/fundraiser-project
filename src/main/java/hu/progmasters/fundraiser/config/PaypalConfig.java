package hu.progmasters.fundraiser.config;

import com.paypal.base.rest.APIContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {

    @Value("${appConfig.paypal.client-id}")
    private String clientId;

    @Value("${appConfig.paypal.client-secret}")
    private String clientSecret;

    @Value("${appConfig.paypal.mode}")
    private String mode;

    @Value("${appConfig.payment.cancel-url}")
    @Getter
    private String cancelUrl;

    @Value("${appConfig.payment.success-url}")
    @Getter
    private String successUrl;

    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }

}