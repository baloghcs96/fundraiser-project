package hu.progmasters.fundraiser.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import hu.progmasters.fundraiser.config.PaypalConfig;
import hu.progmasters.fundraiser.service.PaypalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class PaypalControllerTest {

    @Mock
    private PaypalService paypalService;

    @Mock
    private PaypalConfig paypalConfig;

    @InjectMocks
    private PaypalController paypalController;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void homeShouldReturnPaymentIndexView() {
        String viewName = paypalController.home();
        assertEquals("paymentIndex", viewName);
    }

    @Test
    void createPaymentTest() throws PayPalRESTException {
        Payment payment = new Payment();
        List<Links> links = new ArrayList<>();
        Links link = new Links();
        link.setRel("approval_url");
        link.setHref("http://approval.url");
        links.add(link);
        payment.setLinks(links);

        when(paypalService.createPayment(any(), any(), any(), any(), any(), any(), any())).thenReturn(payment);
        when(paypalConfig.getCancelUrl()).thenReturn("http://cancel.url");
        when(paypalConfig.getSuccessUrl()).thenReturn("http://success.url");

        RedirectView redirectView = paypalController.createPayment("method", "1000.0", "currency", "description");

        verify(paypalService).createPayment(any(), any(), any(), any(), any(), any(), any());
        verify(paypalConfig).getCancelUrl();
        verify(paypalConfig).getSuccessUrl();

        assertEquals("http://approval.url", redirectView.getUrl());
    }

    @Test
    void paymentSuccessShouldReturnSuccessViewWhenPaymentApproved() throws PayPalRESTException {
        Payment mockPayment = mock(Payment.class);
        when(mockPayment.getState()).thenReturn("approved");
        when(paypalService.executePayment(anyString(), anyString())).thenReturn(mockPayment);

        String viewName = paypalController.paymentSuccess("paymentId", "payerId");

        assertEquals("paymentSuccess", viewName);
    }

    @Test
    void paymentSuccessShouldLogErrorAndReturnSuccessViewOnException() throws PayPalRESTException {
        when(paypalService.executePayment(anyString(), anyString())).thenThrow(PayPalRESTException.class);

        String viewName = paypalController.paymentSuccess("invalidPaymentId", "invalidPayerId");

        assertEquals("paymentSuccess", viewName);
    }

    @Test
    void paymentSuccessShouldReturnSuccessViewWhenPaymentStateIsNotApproved() throws PayPalRESTException {
        Payment mockPayment = mock(Payment.class);
        when(mockPayment.getState()).thenReturn("failed");
        when(paypalService.executePayment(anyString(), anyString())).thenReturn(mockPayment);

        String viewName = paypalController.paymentSuccess("paymentId", "payerId");

        assertEquals("paymentSuccess", viewName);
    }

    @Test
    void paymentCancelShouldReturnCancelView() {
        String viewName = paypalController.paymentCancel();
        assertEquals("paymentCancel", viewName);
    }

    @Test
    void paymentErrorShouldReturnErrorView() {
        String viewName = paypalController.paymentError();
        assertEquals("paymentError", viewName);
    }
}