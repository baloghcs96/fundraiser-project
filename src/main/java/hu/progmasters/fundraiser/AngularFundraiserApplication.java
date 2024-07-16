package hu.progmasters.fundraiser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@PropertySource("classpath:apikey.properties")
@EnableScheduling
public class AngularFundraiserApplication {
    public static void main(String[] args) {
        SpringApplication.run(AngularFundraiserApplication.class, args);
    }
}
