package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.dto.outgoing.FundCompletedInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final MessageSource messages;

    public void sendRegistrationConfirmationEmail(String recipientAddress, String appUrl, String token, Locale locale) {
        String subject = "Registration Confirmation";
        String confirmationUrl = appUrl + "/registrationConfirm?token=" + token;
        String text = messages.getMessage("message.regSuccessNeedVerify", new Object[]{token, confirmationUrl}, locale);
        System.out.println(text);
        sendMimeMessage(recipientAddress, subject, text);
    }

    public void sendTransactionVerificationEmail(String recipientAddress, String appUrl, String token, Locale locale) {
        String subject = "Verify your transaction";
        String confirmationUrl = appUrl + "/verificationConfirm?token=" + token;
        String text = messages.getMessage("message.transactionSavedNeedVerify", new String[]{confirmationUrl}, locale);
        sendMimeMessage(recipientAddress, subject, text);
    }

    public void sendGaolCompleteEvent(FundCompletedInfo fundCompletedInfo, String text) {
        sendMimeMessage(fundCompletedInfo.getEmail(), fundCompletedInfo.getSubject(), text);
    }

    public void sendForgottenPasswordResetEmailEvent(String recipientAddress, String appUrl, String token, Locale locale) {
        String subject = "Forgotten Password Reset";
        String confirmationUrl = appUrl + "/newPassword?token=" + token;
        String text = messages.getMessage("message.forgottenPasswordReset", new Object[]{confirmationUrl}, locale);
        sendMimeMessage(recipientAddress, subject, text);
    }

    private void sendMimeMessage(String to, String subject, String text) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("apikey.properties"));
            String fromEmail = prop.getProperty("mail.address");

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException e) {
            log.error("Error while sending email: {}", subject, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        emailSender.send(message);
    }


}