package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.dto.outgoing.FundCompletedInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private final String recipientAddress = "test@example.com";
    private final String appUrl = "http://example.com";
    private final String token = "testToken";
    private final Locale locale = LocaleContextHolder.getLocale();
    @Mock
    private JavaMailSender emailSender;
    @Mock
    private MessageSource messages;
    @Mock
    private MimeMessage mimeMessage;
    @Mock
    private MimeMessageHelper mimeMessageHelper;
    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        // Konfigurálja a MessageSource mock-ot, hogy visszaadjon egy szöveges értéket
        when(messages.getMessage(any(), any(), eq(locale)))
                .thenReturn("Teszt üzenet.");
    }

    @Test
    public void testSendRegistrationConfirmationEmail() throws MessagingException {
        emailService.sendRegistrationConfirmationEmail(recipientAddress, appUrl, token, locale);
        Mockito.verify(emailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }

    @Test
    public void testSendTransactionVerificationEmail() throws MessagingException {
        emailService.sendTransactionVerificationEmail(recipientAddress, appUrl, token, locale);
        Mockito.verify(emailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }

    @Test
    void sendGaolCompleteEvent() throws Exception {
        FundCompletedInfo fundCompletedInfo = new FundCompletedInfo("Fund Title", "test@example.com", "Subject");

        emailService.sendGaolCompleteEvent(fundCompletedInfo, "Your fund has been completed.");

        verify(emailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendForgottenPasswordResetEmailEvent() throws Exception {
        emailService.sendForgottenPasswordResetEmailEvent(recipientAddress, appUrl, token, locale);
        Mockito.verify(emailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }
}