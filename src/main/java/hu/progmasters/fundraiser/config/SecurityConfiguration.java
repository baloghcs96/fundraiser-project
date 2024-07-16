package hu.progmasters.fundraiser.config;

import hu.progmasters.fundraiser.service.JpaUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String A = "ROLE_ADMIN";
    private static final String U = "ROLE_USER";
    private static final String UN = "ROLE_UNVERIFIED";
    private static final String GS = "GRADE_SILVER";
    private static final String GG = "GRADE_GOLD";
    private static final String GP = "GRADE_PLATINUM";
    private final PasswordEncoder passwordEncoder;
    private final JpaUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic()
                .and().authorizeRequests()
                .antMatchers("/background.png").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/api/fundraiser/").permitAll()
                .antMatchers("/api/fundraiser/account/save").permitAll()
                .antMatchers("/api/fundraiser/account/saveButton").permitAll()
                .antMatchers("/api/fundraiser/account/saveForm").permitAll()
                .antMatchers("/api/fundraiser/account/saveForm/registrationConfirm").permitAll()
                .antMatchers("/api/fundraiser/account/save/registrationConfirm").permitAll()
                .antMatchers("/api/fundraiser/account/save/registrationConfirm/**").permitAll()
                .antMatchers("/api/fundraiser/account/createNewToken").hasAnyAuthority(A, UN)
                .antMatchers("/api/fundraiser/account/logout").hasAnyAuthority(A, U, UN)
                .antMatchers("/api/fundraiser/account/loginButton").permitAll()
                .antMatchers("/api/fundraiser/account/login").permitAll()
                .antMatchers("/api/fundraiser/account/currencies").permitAll()
                .antMatchers("/api/fundraiser/account/myAccount").hasAnyAuthority(A, U, UN)
                .antMatchers("/api/fundraiser/account/update").hasAnyAuthority(A, U, UN)
                .antMatchers("/api/fundraiser/account/balance").hasAnyAuthority(A, U)
                .antMatchers("/api/fundraiser/account/resetPassword").hasAnyAuthority(A, U, UN)
                .antMatchers("/api/fundraiser/account/resetPasswordForm").permitAll()
                .antMatchers("/api/fundraiser/account/resetPasswordForm/**").permitAll()
                .antMatchers("/api/fundraiser/account/resetForgottenPassword").permitAll()
                .antMatchers("/api/fundraiser/account/resetForgottenPassword/newPassword").permitAll()
                .antMatchers("/api/fundraiser/account/resetForgottenPassword/newPassword/**").permitAll()
                .antMatchers("/api/fundraiser/account/resetForgottenPassword/confirm").permitAll()
                .antMatchers("/api/fundraiser/account/checkAdminRole").hasAnyAuthority(A)
                .antMatchers("/api/fundraiser/account/checkUserRole").hasAnyAuthority(U)

                .antMatchers("/api/fundraiser/exchange/latest").hasAnyAuthority(A)

                .antMatchers("/api/fundraiser/fund").hasAnyAuthority(A, U)
                .antMatchers("/api/fundraiser/fund/update/**").hasAnyAuthority(A, U)
                .antMatchers("/api/fundraiser/fund/**").permitAll()

                .antMatchers("/api/fundraiser/transaction/verificationConfirm").permitAll()
                .antMatchers("/api/fundraiser/transaction/").hasAnyAuthority(A, U)
                .antMatchers("/api/fundraiser/transaction/**").hasAnyAuthority(A, U)

                .antMatchers("/api/fundraiser/flashSale/saveFlashSaleItem").hasAnyAuthority(A)
                .antMatchers("/api/fundraiser/flashSale/getFlashSaleItem").hasAnyAuthority(A, GS, GG, GP)

                .antMatchers("/api/fundraiser/purchase/**").hasAnyAuthority(A, GS, GG, GP)

                .antMatchers("/api/fundraiser/badge/**").hasAnyAuthority(A)

                .antMatchers("/api/fundraiser/payment/**").hasAnyAuthority(A, U)

                .anyRequest().denyAll()

                .and().logout()
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}