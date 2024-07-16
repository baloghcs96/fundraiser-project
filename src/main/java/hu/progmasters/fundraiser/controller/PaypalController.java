package hu.progmasters.fundraiser.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import hu.progmasters.fundraiser.config.PaypalConfig;
import hu.progmasters.fundraiser.service.PaypalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/fundraiser/payment")
@RequiredArgsConstructor
@Slf4j
public class PaypalController {

    private final PaypalService paypalService;
    private final PaypalConfig paypalConfig;

    @GetMapping()
    public String home() {
        return "paymentIndex";
    }

    @PostMapping("/create")
    public RedirectView createPayment(
            @RequestParam("method") String method,
            @RequestParam("amount") String amount,
            @RequestParam("currency") String currency,
            @RequestParam("description") String description
    ) {
        try {
            Payment payment = paypalService.createPayment(
                    Double.valueOf(amount),
                    currency,
                    method,
                    "sale",
                    description,
                    paypalConfig.getCancelUrl(),
                    paypalConfig.getSuccessUrl());

            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return new RedirectView(link.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            log.error("Error happened during payment creation!", e);
        }

        return new RedirectView("/api/fundraiser/payment/error");
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                paypalService.updateAccountBalance(payment);
                return "paymentSuccess";
            }
        } catch (PayPalRESTException e) {
            log.error("Error happened during payment execution!", e);
        }
        return "paymentSuccess";
    }

    @GetMapping("/cancel")
    public String paymentCancel() {
        return "paymentCancel";
    }

    @GetMapping("/error")
    public String paymentError() {
        return "paymentError";
    }
}
