package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.dto.incoming.PurchaseCreateCommand;
import hu.progmasters.fundraiser.dto.outgoing.PurchaseInfo;
import hu.progmasters.fundraiser.service.AccountService;
import hu.progmasters.fundraiser.service.PurchaseService;
import hu.progmasters.fundraiser.service.SecurityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/fundraiser/purchase")
@AllArgsConstructor
@Slf4j
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final SecurityService securityService;
    private final AccountService accountService;

    @PostMapping("/savePurchase")
    @ResponseStatus(CREATED)
    public void savePurchase(@RequestBody PurchaseCreateCommand command) {
        log.info("Http request, POST /api/fundraiser/purchase");
        Authentication authentication = securityService.getAuthentication();
        Long accountId = accountService.getLoggedInUserId(authentication);
        purchaseService.savePurchase(command, accountId);
    }

    @GetMapping("/myPurchase")
    @ResponseStatus(OK)
    public List<PurchaseInfo> getPurchase() {
        log.info("Http request, GET /api/fundraiser/purchase");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long accountId = accountService.getLoggedInUserId(authentication);
        return purchaseService.getPurchase(accountId);
    }
}
