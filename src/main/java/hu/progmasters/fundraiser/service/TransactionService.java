package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.Fund;
import hu.progmasters.fundraiser.domain.entity.Transaction;
import hu.progmasters.fundraiser.domain.entity.token.TransactionVerificationToken;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.domain.enumeration.TransactionState;
import hu.progmasters.fundraiser.dto.incoming.TransactionFilterCommand;
import hu.progmasters.fundraiser.dto.incoming.TransactionSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.*;
import hu.progmasters.fundraiser.event.OnTransactionCreateCompleteEvent;
import hu.progmasters.fundraiser.exception.FundIsClosedException;
import hu.progmasters.fundraiser.exception.InvalidScheduleTimeException;
import hu.progmasters.fundraiser.exception.NotEnoughBalanceToTransferException;
import hu.progmasters.fundraiser.exception.TransactionToYourselfException;
import hu.progmasters.fundraiser.repository.TransactionRepository;
import hu.progmasters.fundraiser.service.token.TransactionVerificationTokenService;
import hu.progmasters.fundraiser.util.MathUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TransactionService {

    private static final int MAX_ATTEMPT_COUNT = 2;
    private static final int EXP_EXCHANGE_RATE = 2;
    private static final int COIN_EXCHANGE_RATE = 10;
    //    private static final int MIN_TIME_DIFFERENCE = 10;
    private final ModelMapper modelMapper;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final FundService fundService;
    private final ExchangeService exchangeService;
    private final TransactionVerificationTokenService transactionVerificationTokenService;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionVerifyMessageInfo saveTransaction(TransactionSaveUpdateCommand command, Long accountId, HttpServletRequest request) {
        Transaction transaction = modelMapper.map(command, Transaction.class);
        Account account = accountService.findAccountById(accountId);
        Fund fund = fundService.findFundById(command.getFundId());

        checkIsFundClosed(fund);
        checkTransactionToYourself(account.getAccountId(), fund.getAccount().getAccountId());
        enoughBalanceCheck(account.getBalance(), command.getSentAmount());
        checkSetTransactionTime(transaction, command);

        setTransaction(transaction, account, fund);

        transactionRepository.save(transaction);

        TransactionCreateInfo transactionCreateInfo = new TransactionCreateInfo(transaction.getTransactionId(), account.getEmail());
        publishEventSendTransactionVerificationEmail(request, transactionCreateInfo);

        String message = "Please verify your transaction with link in your email: " + account.getEmail() + "!";
        return new TransactionVerifyMessageInfo(message);
    }

    public void verificationTransfer(String token) {
        TransactionVerificationToken transactionVerificationToken = transactionVerificationTokenService.getTransactionVerificationTokenByToken(token);
        transactionVerificationTokenService.isTransactionTokenExpired(transactionVerificationToken);
        Transaction transaction = transactionRepository.findById(transactionVerificationToken.getTransactionId()).orElseThrow();
        transactionVerificationTokenService.deleteTransactionToken(transactionVerificationToken);
        setTransactionType(transaction);
    }

    public List<DonatorInfo> getDonatorInfosByFundId(Long fundId) {
        return transactionRepository.findAllByFundId(fundId)
                .stream()
                .collect(Collectors.groupingBy(Transaction::getSenderAccount, Collectors.summingDouble(Transaction::getSentAmount)))
                .entrySet()
                .stream()
                .map(entry -> new DonatorInfo(entry
                        .getKey().getAccountId(), entry.getKey().getAccountName(), entry.getValue())).collect(Collectors.toList());
    }

    public List<TransactionAccountInfo> getTransactionInfosByAccountId(Long accountId) {
        List<Transaction> myTransactions = transactionRepository.findAllBySenderAccountId(accountId);
        return myTransactions.stream().map(transaction ->
                modelMapper.map(transaction, TransactionAccountInfo.class)).collect(Collectors.toList());
    }

    public List<TransactionFundInfo> getTransactionsInfosByFundId(Long fundId) {
        List<Transaction> transactions = transactionRepository.findAllByFundId(fundId);
        return transactions.stream().map(transaction -> modelMapper.map(transaction, TransactionFundInfo.class)).collect(Collectors.toList());
    }

    public List<FilteredTransactionInfo> getFilteredTransactionInfosByAccountId(Long accountId, TransactionFilterCommand filterCommand) {
        Account account = accountService.findAccountById(accountId);
        List<Transaction> transactions = transactionRepository.findAllBySenderAccountId(account.getAccountId());
        return transactions.stream().filter(transaction -> filterTransaction(transaction, filterCommand)).map(transaction -> {
            FilteredTransactionInfo filteredTransactionInfo = modelMapper.map(transaction, FilteredTransactionInfo.class);
            filteredTransactionInfo.setTargetFundTitle(transaction.getTargetFund().getTitle());

//            COMMENTED BECAUSE USE MathUtil class
//            double receivedAmount = transaction.getReceivedAmount();
//            receivedAmount = Math.round(receivedAmount * Math.pow(10, 2)) / Math.pow(10, 2);

            filteredTransactionInfo.setReceivedAmount(MathUtil.roundToTwoDecimal(transaction.getReceivedAmount()));
            return filteredTransactionInfo;
        }).collect(Collectors.toList());
    }

    @Scheduled(cron = "${scheduled.transaction.cron}")
    public void scheduleTransaction() {
        List<Transaction> pendingTransactions = transactionRepository.findAllByTransactionState(TransactionState.PENDING);
        for (Transaction transaction : pendingTransactions) {
            if (transaction.getTransactionTime().isBefore(LocalDateTime.now())) {
                Account account = transaction.getSenderAccount();
                try {
                    enoughBalanceCheck(account.getBalance(), transaction.getSentAmount());
                    performTransaction(transaction);
                } catch (NotEnoughBalanceToTransferException e) {
                    transaction.setAttemptCount(transaction.getAttemptCount() + 1);
                    if (transaction.getAttemptCount() == MAX_ATTEMPT_COUNT) {
                        transaction.setTransactionState(TransactionState.FAILED);
                    }
                }
            }
        }
    }

    private void checkIsFundClosed(Fund fund) {
        if (!fund.getOpenForDonation()) {
            throw new FundIsClosedException(fund.getTitle());
        }
    }

    private void checkTransactionToYourself(Long accountId, Long fundId) {
        if (accountId.equals(fundId)) {
            throw new TransactionToYourselfException(accountId, fundId);
        }
    }

    private void enoughBalanceCheck(Double balance, Double sentAmount) {
        if (balance < sentAmount) {
            throw new NotEnoughBalanceToTransferException(balance);
        }
    }

    private void setTransaction(Transaction transaction, Account account, Fund fund) {
        double exchangeRate = determineExchangeRate(account.getCurrency(), fund.getCurrency());
        double receivedAmount = transaction.getSentAmount() * exchangeRate;
        transaction.setSenderAccount(account);
        transaction.setSentCurrency(account.getCurrency());
        transaction.setTargetFund(fund);
        transaction.setReceivedAmount(MathUtil.roundToTwoDecimal(receivedAmount));
        transaction.setReceivedCurrency(fund.getCurrency());
        transaction.setTransactionState(TransactionState.NOT_VERIFIED);
    }

    private void checkSetTransactionTime(Transaction transaction, TransactionSaveUpdateCommand command) {
        if (command.getTransactionTime() != null && command.getTransactionTime().isBefore(LocalDateTime.now())) {
            throw new InvalidScheduleTimeException(transaction.getTransactionTime());
        }
        transaction.setTransactionTime(command.getTransactionTime());
    }

    private double determineExchangeRate(Currency accountCurrency, Currency fundCurrency) {
        if (accountCurrency == fundCurrency) {
            return 1.0;
        } else if (accountCurrency == Currency.EUR) {
            return exchangeService.getRateByCurrency(fundCurrency);
        } else if (fundCurrency == Currency.EUR) {
            return 1 / exchangeService.getRateByCurrency(accountCurrency);
        } else {
            double exchangeRateAccount = exchangeService.getRateByCurrency(accountCurrency);
            double exchangeRateFund = exchangeService.getRateByCurrency(fundCurrency);
            return exchangeRateFund / exchangeRateAccount;
        }
    }

    private void setTransactionType(Transaction transaction) {
        if (transaction.getTransactionTime() == null) {
            transaction.setTransactionTime(LocalDateTime.now());
            performTransaction(transaction);
        } else if (transaction.getTransactionTime().isAfter(LocalDateTime.now())) {
            transaction.setTransactionState(TransactionState.PENDING);
        }
    }

    private void performTransaction(Transaction transaction) {
        Fund fund = transaction.getTargetFund();
        transaction.setTransactionState(TransactionState.COMPLETED);
        transaction.getSenderAccount().setBalance(transaction.getSenderAccount().getBalance() - transaction.getSentAmount());
        fund.setCurrentAmount(MathUtil.roundToTwoDecimal(fund.getCurrentAmount() + transaction.getReceivedAmount()));
        upgradeGradeAndExperienceAndCoin(transaction.getSenderAccount(), transaction);
        fundService.isFundGoalCompleted(fund);
    }

    private void upgradeGradeAndExperienceAndCoin(Account account, Transaction transaction) {
        double sentAmountInEur = exchangeService.convertToEur(account.getCurrency(), transaction.getSentAmount());
        int experiencePoints = (int) (sentAmountInEur * EXP_EXCHANGE_RATE);
        account.setExperiencePoints(account.getExperiencePoints() + experiencePoints);
        int calculateCoin = experiencePoints / COIN_EXCHANGE_RATE;
        account.setCoin(account.getCoin() + calculateCoin);
        Grade grade = accountService.determineGrade(account.getExperiencePoints());
        account.setGrade(grade);
    }

    private void publishEventSendTransactionVerificationEmail(HttpServletRequest request, TransactionCreateInfo created) {
        eventPublisher.publishEvent(new OnTransactionCreateCompleteEvent(created, request.getRequestURL().toString(), request.getLocale()));
    }

    private boolean filterTransaction(Transaction transaction, TransactionFilterCommand filterCommand) {
        if (filterCommand.getFundTitle() != null && !transaction.getTargetFund().getTitle().equals(filterCommand.getFundTitle())) {
            return false;
        }
        if (filterCommand.getSentAmount() != null && transaction.getSentAmount() < filterCommand.getSentAmount()) {
            return false;
        }
        return filterCommand.getTransactionDate() == null || (transaction.getTransactionTime().toLocalDate().isEqual
                (filterCommand.getTransactionDate()) || transaction.getTransactionTime().toLocalDate().
                isAfter(filterCommand.getTransactionDate()));
    }

    /*
    private int validTransactionDifference(LocalDateTime transactionTime) {
        return (int) ChronoUnit.MINUTES.between(LocalDateTime.now(), transactionTime);
    }
    */
}
