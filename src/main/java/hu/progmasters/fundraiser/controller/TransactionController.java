package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.dto.incoming.TransactionFilterCommand;
import hu.progmasters.fundraiser.dto.incoming.TransactionSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.*;
import hu.progmasters.fundraiser.service.AccountService;
import hu.progmasters.fundraiser.service.SecurityService;
import hu.progmasters.fundraiser.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/fundraiser/transaction")
@AllArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    private final SecurityService securityService;
    private final AccountService accountService;

    @PostMapping()
    @ResponseStatus(CREATED)
    public TransactionVerifyMessageInfo saveTransaction(@Valid @RequestBody TransactionSaveUpdateCommand command, HttpServletRequest request) {
        log.info("Http request, POST /api/fundraiser/transaction - saveTransaction - command: {}", command.toString());
        Authentication authentication = securityService.getAuthentication();
        Long accountId = accountService.getLoggedInUserId(authentication);
        return transactionService.saveTransaction(command, accountId, request);
    }

    @GetMapping("/verificationConfirm")
    @ResponseStatus(OK)
    public String verificationTransfer(@RequestParam("token") String token) {
        transactionService.verificationTransfer(token);
        log.info("Http request, GET /api/fundraiser/transaction/verificationConfirm - token: {}", token);
        return "Transaction verified!";
    }

    @GetMapping("/fund/transactions/{fundId}")
    @ResponseStatus(OK)
    public List<TransactionFundInfo> getTransactionsInfosByFundId(@PathVariable Long fundId) {
        log.info("Http request, GET /api/fundraiser/transaction/fund/transactions/{}", fundId);
        return transactionService.getTransactionsInfosByFundId(fundId);
    }

    @GetMapping("/fund/{fundId}")
    public List<DonatorInfo> getDonatorInfosByFundId(@PathVariable Long fundId) {
        log.info("Http request, GET /api/fundraiser/transaction/fund/{}", fundId);
        return transactionService.getDonatorInfosByFundId(fundId);
    }

    @GetMapping("/account/filter/{accountId}")
    @ResponseStatus(OK)
    public List<FilteredTransactionInfo> getFilteredTransactionsByAccountId(@PathVariable("accountId") Long accountId,
                                                                            @RequestParam(required = false) String fundTitle,
                                                                            @RequestParam(required = false) Double minSentAmount,
                                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate transactionDate) {
        log.info("Http request,GET /api/fundraiser/transaction/account/filtered/{}", accountId);
        TransactionFilterCommand filterCommand = new TransactionFilterCommand(fundTitle, minSentAmount, transactionDate);
        return transactionService.getFilteredTransactionInfosByAccountId(accountId, filterCommand);
    }

    @GetMapping("/account/{accountId}")
    @ResponseStatus(OK)
    public List<TransactionAccountInfo> getTransactionInfosByAccountId(@PathVariable("accountId") Long accountId) {
        log.info("Http request, GET /api/fundraiser/transaction/account/{}", accountId);
        return transactionService.getTransactionInfosByAccountId(accountId);
    }
}

