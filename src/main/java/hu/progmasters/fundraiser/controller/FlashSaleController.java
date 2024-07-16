package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.dto.incoming.FlashSaleItemSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.FlashSaleCreateInfo;
import hu.progmasters.fundraiser.dto.outgoing.FlashSaleItemInfo;
import hu.progmasters.fundraiser.service.AccountService;
import hu.progmasters.fundraiser.service.FlashSaleService;
import hu.progmasters.fundraiser.service.SecurityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/fundraiser/flashSale")
@AllArgsConstructor
@Slf4j
public class FlashSaleController {

    private final FlashSaleService flashSaleService;
    private final AccountService accountService;
    private final SecurityService securityService;

    @PostMapping("/saveFlashSaleItem")
    @ResponseStatus(CREATED)
    public FlashSaleCreateInfo saveFlashSaleItem(@RequestBody FlashSaleItemSaveUpdateCommand command) {
        log.info("Flash sale item creation requested, POST /api/fundraiser/flashSale: {}", command.toString());
        return flashSaleService.saveFlashSaleItem(command);
    }

    @GetMapping("/getFlashSaleItem")
    @ResponseStatus(OK)
    public List<FlashSaleItemInfo> getFlashSaleItemByAccountIdAndGrade() {
        log.info("Flash sale item requested, GET /api/fundraiser/flashSale");
        Authentication authentication = securityService.getAuthentication();
        Long accountId = accountService.getLoggedInUserId(authentication);
        return flashSaleService.getFlashSaleItemByAccountIdAndGrade(accountId);
    }

}
