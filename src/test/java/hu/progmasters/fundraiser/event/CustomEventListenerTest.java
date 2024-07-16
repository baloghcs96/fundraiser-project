package hu.progmasters.fundraiser.event;

import hu.progmasters.fundraiser.dto.outgoing.AccountInfo;
import hu.progmasters.fundraiser.dto.outgoing.FundCompletedInfo;
import hu.progmasters.fundraiser.dto.outgoing.TransactionCreateInfo;
import hu.progmasters.fundraiser.service.EmailService;
import hu.progmasters.fundraiser.service.token.AccountVerificationTokenService;
import hu.progmasters.fundraiser.service.token.PasswordResetVerificationTokenService;
import hu.progmasters.fundraiser.service.token.TransactionVerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;

import java.lang.reflect.Method;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomEventListenerTest {

    @Mock
    private AccountVerificationTokenService accountVerificationTokenService;

    @Mock
    private TransactionVerificationTokenService transactionVerificationTokenService;
    @Mock
    private EmailService emailService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CustomEventListener customEventListener;

    @Mock
    private PasswordResetVerificationTokenService passwordResetVerificationTokenService;

    @Mock
    private MessageSource messageSource;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void confirmRegistrationTest() throws Exception {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(1L);
        accountInfo.setEmail("test@example.com");
        OnRegistrationCompleteEvent event = new OnRegistrationCompleteEvent(accountInfo, "http://example.com", java.util.Locale.ENGLISH);
        String token = "token123";
        when(accountVerificationTokenService.createAccountVerificationToken(accountInfo.getAccountId())).thenReturn(token);

        Method method = CustomEventListener.class.getDeclaredMethod("confirmRegistration", OnRegistrationCompleteEvent.class);
        method.setAccessible(true);
        method.invoke(customEventListener, event);

        verify(emailService).sendRegistrationConfirmationEmail(accountInfo.getEmail(), event.getAppUrl(), token, event.getLocale());
    }

    @Test
    void newTokenTest() throws Exception {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(2L);
        accountInfo.setEmail("user@example.com");
        OnRequestedNewTokenEvent event = new OnRequestedNewTokenEvent(accountInfo, "http://example.com", java.util.Locale.ENGLISH);
        String token = "newToken123";
        when(accountVerificationTokenService.createAccountVerificationToken(accountInfo.getAccountId())).thenReturn(token);

        Method method = CustomEventListener.class.getDeclaredMethod("newToken", OnRequestedNewTokenEvent.class);
        method.setAccessible(true);
        method.invoke(customEventListener, event);

        verify(emailService).sendRegistrationConfirmationEmail(accountInfo.getEmail(), event.getAppUrl(), token, event.getLocale());
    }

    @Test
    void confirmTransactionTest() throws Exception {
        // Prepare test data
        TransactionCreateInfo transactionCreateInfo = new TransactionCreateInfo(1L, "user@example.com");
        OnTransactionCreateCompleteEvent event = new OnTransactionCreateCompleteEvent(transactionCreateInfo, "http://example.com", java.util.Locale.ENGLISH);
        String token = "transactionToken123";

        // Stubbing method calls
        when(transactionVerificationTokenService.createTransactionVerificationToken(transactionCreateInfo.getTransactionId())).thenReturn(token);

        // Invoke method
        Method method = CustomEventListener.class.getDeclaredMethod("confirmTransaction", OnTransactionCreateCompleteEvent.class);
        method.setAccessible(true);
        method.invoke(customEventListener, event);

        // Verify interactions
        verify(emailService).sendTransactionVerificationEmail(transactionCreateInfo.getEmail(), event.getAppUrl(), token, event.getLocale());
    }

    @Test
    void goalCompleteEventTest() throws Exception {
        FundCompletedInfo fundCompletedInfo = new FundCompletedInfo();
        fundCompletedInfo.setFundTitle("Goal Achieved");
        OnGoalCompletedEvent event = new OnGoalCompletedEvent(fundCompletedInfo, "Goal Completed");
        when(messageSource.getMessage(eq("Goal Completed"), any(), eq(Locale.ENGLISH))).thenReturn("Goal Completed Message");

        Method method = CustomEventListener.class.getDeclaredMethod("goalCompleteEvent", OnGoalCompletedEvent.class);
        method.setAccessible(true);
        method.invoke(customEventListener, event);

        verify(emailService).sendGaolCompleteEvent(eq(fundCompletedInfo), eq("Goal Completed Message"));
    }

    @Test
    void forgottenPasswordResetTest() throws Exception {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(3L);
        accountInfo.setEmail("forgot@example.com");
        OnForgottenPasswordResetEvent event = new OnForgottenPasswordResetEvent(accountInfo, "http://example.com", java.util.Locale.ENGLISH);
        String token = "resetToken123";
        when(passwordResetVerificationTokenService.createPasswordResetVerificationToken(accountInfo.getAccountId())).thenReturn(token);

        Method method = CustomEventListener.class.getDeclaredMethod("forgottenPasswordReset", OnForgottenPasswordResetEvent.class);
        method.setAccessible(true);
        method.invoke(customEventListener, event);

        verify(emailService).sendForgottenPasswordResetEmailEvent(accountInfo.getEmail(), event.getAppUrl(), token, event.getLocale());
    }


}