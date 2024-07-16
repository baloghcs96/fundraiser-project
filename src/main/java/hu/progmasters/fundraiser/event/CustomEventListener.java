package hu.progmasters.fundraiser.event;

import hu.progmasters.fundraiser.dto.outgoing.AccountInfo;
import hu.progmasters.fundraiser.dto.outgoing.FundCompletedInfo;
import hu.progmasters.fundraiser.dto.outgoing.TransactionCreateInfo;
import hu.progmasters.fundraiser.service.EmailService;
import hu.progmasters.fundraiser.service.token.AccountVerificationTokenService;
import hu.progmasters.fundraiser.service.token.PasswordResetVerificationTokenService;
import hu.progmasters.fundraiser.service.token.TransactionVerificationTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static java.util.Locale.ENGLISH;

@Slf4j
@Component
@AllArgsConstructor
public class CustomEventListener {

    private static final Locale LOCALE = ENGLISH;
    private final MessageSource messageSource;
    private final PasswordResetVerificationTokenService passwordResetVerificationTokenService;
    private AccountVerificationTokenService accountVerificationTokenService;
    private TransactionVerificationTokenService transactionVerificationTokenService;
    private EmailService emailService;

    @EventListener
    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        log.info("Registration confirmation requested, POST /api/fundraiser/account/save/registrationConfirm");
        AccountInfo accountInfo = event.getAccountInfo();
        String token = accountVerificationTokenService.createAccountVerificationToken(accountInfo.getAccountId());
        String recipientAddress = accountInfo.getEmail();
        emailService.sendRegistrationConfirmationEmail(recipientAddress, event.getAppUrl(), token, event.getLocale());
    }

    @EventListener
    private void newToken(OnRequestedNewTokenEvent event) {
        AccountInfo accountInfo = event.getAccountInfo();
        String token = accountVerificationTokenService.createAccountVerificationToken(accountInfo.getAccountId());
        String recipientAddress = accountInfo.getEmail();
        emailService.sendRegistrationConfirmationEmail(recipientAddress, event.getAppUrl(), token, event.getLocale());
    }

    @EventListener
    private void confirmTransaction(OnTransactionCreateCompleteEvent event) {
        log.info("Registration confirmation requested, POST /api/fundraiser/transaction/verificationConfirm");
        TransactionCreateInfo transactionCreateInfo = event.getTransactionCreateInfo();
        String token = transactionVerificationTokenService.createTransactionVerificationToken(transactionCreateInfo.getTransactionId());
        String recipientAddress = transactionCreateInfo.getEmail();
        emailService.sendTransactionVerificationEmail(recipientAddress, event.getAppUrl(), token, event.getLocale());
    }

    @EventListener
    private void goalCompleteEvent(OnGoalCompletedEvent event) {
        log.info("Goal completed event requested, POST /api/fundraiser/fund/goalComplete");
        FundCompletedInfo fundCompletedInfo = event.getFundCompletedInfo();
        emailService.sendGaolCompleteEvent(fundCompletedInfo, messageSource.getMessage(event.getMessage(), new String[]{fundCompletedInfo.getFundTitle()}, LOCALE));
    }

    @EventListener
    private void forgottenPasswordReset(OnForgottenPasswordResetEvent event) {
        log.info("Forgotten password reset requested, POST /api/fundraiser/account/forgottenPasswordReset");
        String token = passwordResetVerificationTokenService.createPasswordResetVerificationToken(event.getAccountInfo().getAccountId());
        String recipientAddress = event.getAccountInfo().getEmail();
        emailService.sendForgottenPasswordResetEmailEvent(recipientAddress, event.getAppUrl(), token, event.getLocale());
    }
}
