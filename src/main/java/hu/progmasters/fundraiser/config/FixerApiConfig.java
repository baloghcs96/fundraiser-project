package hu.progmasters.fundraiser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FixerApiConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
