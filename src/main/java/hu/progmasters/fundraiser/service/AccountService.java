package hu.progmasters.fundraiser.service;

import hu.progmasters.fundraiser.domain.entity.Account;
import hu.progmasters.fundraiser.domain.entity.token.AccountVerificationToken;
import hu.progmasters.fundraiser.domain.entity.token.PasswordResetVerificationToken;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.domain.enumeration.UserRole;
import hu.progmasters.fundraiser.dto.incoming.AccountChangePassword;
import hu.progmasters.fundraiser.dto.incoming.AccountGetEmailForForgottenPasswordCommand;
import hu.progmasters.fundraiser.dto.incoming.AccountSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.AccountInfo;
import hu.progmasters.fundraiser.dto.outgoing.ChooseCurrency;
import hu.progmasters.fundraiser.event.OnForgottenPasswordResetEvent;
import hu.progmasters.fundraiser.event.OnRegistrationCompleteEvent;
import hu.progmasters.fundraiser.event.OnRequestedNewTokenEvent;
import hu.progmasters.fundraiser.exception.*;
import hu.progmasters.fundraiser.repository.AccountRepository;
import hu.progmasters.fundraiser.service.token.AccountVerificationTokenService;
import hu.progmasters.fundraiser.service.token.PasswordResetVerificationTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CurrencyService currencyService;
    private final AccountVerificationTokenService accountVerificationTokenService;
    private final PasswordResetVerificationTokenService passwordResetVerificationTokenService;
    private final BadgeService badgeService;
    private final SecurityService securityService;
    private final ExchangeService exchangeService;

    public AccountInfo saveAccount(AccountSaveUpdateCommand command, HttpServletRequest request) {
        log.info("Account creation requested: {}", command.getAccountName());
        checkUsernameAndEmail(command.getAccountName(), command.getEmail());
        command.setPassword(passwordEncoder.encode(command.getPassword()));
        Account account = modelMapper.map(command, Account.class);
        account.setRoles(List.of(UserRole.ROLE_UNVERIFIED));
        accountRepository.save(account);
        AccountInfo registered = modelMapper.map(account, AccountInfo.class);
        registered.setBadge(badgeService.getBadgeByGrade(account.getGrade()).getImageUrl());
        publishEventSendVerificationEmail(request, registered);
        return registered;
    }

    private void checkUsernameAndEmail(String accountName, String email) {
        log.info("Checking username and email");
        if (accountRepository.findByAccountName(accountName).isPresent()) {
            throw new AccountAlreadyExistByNameException(accountName);
        }
        if (accountRepository.existsByEmail(email)) {
            throw new AccountAlreadyExistByEmailException(email);
        }
    }

    public Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundByIdException(accountId));
    }

    public Account findAccountByName(String name) {
        log.info("Account search by name requested: {}", name);
        Account account = accountRepository.findByAccountName(name).orElseThrow(
                () -> new AccountNotFoundByNameException(name));
        return account;
    }

    public Long getLoggedInUserId(Authentication authentication) {
        String name = authentication.getName();
        return accountRepository.findByAccountName(name).orElseThrow(() -> new AccountNotFoundByNameException(name)).getAccountId();
    }

    public void confirmRegistration(String token) {
        AccountVerificationToken accountVerificationToken = accountVerificationTokenService.getAccountVerificationTokenByToken(token);
        accountVerificationTokenService.isAccountTokenExpired(accountVerificationToken);
        setAccountVerified(accountVerificationToken.getAccountId());
        accountVerificationTokenService.deleteAccountToken(accountVerificationToken);
        log.info("Account verified, token deleted");
    }

    public AccountInfo updateAccount(Long accountId, AccountSaveUpdateCommand command, HttpServletRequest request) {
        Account accountToUpdate = findAccountById(accountId);
        if (!accountToUpdate.getEmail().equals(command.getEmail())) {
            accountToUpdate.setEmail(command.getEmail());
            accountToUpdate.setVerified(false);
            publishEventSendVerificationEmail(request, modelMapper.map(accountToUpdate, AccountInfo.class));
        }
        accountToUpdate.setAccountName(accountToUpdate.getAccountName());
        accountToUpdate.setPassword(passwordEncoder.encode(command.getPassword()));
        AccountInfo accountInfo = modelMapper.map(accountToUpdate, AccountInfo.class);
        accountInfo.setBadge(badgeService.getBadgeByGrade(accountToUpdate.getGrade()).getImageUrl());
        return accountInfo;
    }

    public void updateBalanceFromPaypal(double total, String currency) {
        Account account = findAccountById(getLoggedInUserId(securityService.getAuthentication()));
        if (account.getCurrency().toString().equals(currency)) {
            account.setBalance(account.getBalance() + total);
        } else {
            account.setBalance(account.getBalance() + exchangeService.convert(Currency.valueOf(currency), account.getCurrency(), total));
        }
    }

    public Grade determineGrade(int experiencePoints) {
        Grade grade = Grade.BRONZE;
        if (experiencePoints > Grade.PLATINUM.getMinPoints()) {
            grade = Grade.PLATINUM;
        } else if (experiencePoints > Grade.GOLD.getMinPoints()) {
            grade = Grade.GOLD;
        } else if (experiencePoints > Grade.SILVER.getMinPoints()) {
            grade = Grade.SILVER;
        }
        return grade;
    }

    public void createNewAccountToken(Authentication authentication, HttpServletRequest request) {
        Account account = findAccountByName(authentication.getName());
        if (account.isVerified()) {
            log.info("Account is already verified");
            throw new AccountAlreadyVerifiedException(account.getEmail());
        }
        accountVerificationTokenService.createAccountVerificationToken(account.getAccountId());
        StringBuffer requestURL = request.getRequestURL();
        String requestURI = request.getRequestURI();
        String basePath = requestURI.substring(0, requestURI.lastIndexOf("/"));
        String baseUrl = requestURL.substring(0, requestURL.length() - requestURI.length()) + basePath + "/save";
        eventPublisher.publishEvent(new OnRequestedNewTokenEvent(modelMapper.map(account, AccountInfo.class), baseUrl, request.getLocale()));
    }

    public ChooseCurrency getAvailableCurrenciesForNewAccount() {
        return currencyService.getAvailableCurrencies();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public AccountInfo getAccountInfo(Long id) {
        Account account = findAccountById(id);
        AccountInfo accountInfo = modelMapper.map(account, AccountInfo.class);
        accountInfo.setBadge(badgeService.getBadgeByGrade(account.getGrade()).getImageUrl());
        return accountInfo;
    }

    public void resetPassword(Authentication authentication, AccountChangePassword command) {
        Account account = findAccountByName(authentication.getName());
        if (!passwordEncoder.matches(account.getPassword(), command.getNewPassword())) {
            log.info("Password not different");
            throw new PasswordNotDifferentException();
        }
        account.setPassword(passwordEncoder.encode(command.getNewPassword()));
        accountRepository.save(account);
    }

    public void resetForgottenPassword(HttpServletRequest request, AccountGetEmailForForgottenPasswordCommand command) {
        Account account = accountRepository.findByEmail(command.getEmail()).orElseThrow(() -> new AccountNotFoundByEmailException(command.getEmail()));
        eventPublisher.publishEvent(new OnForgottenPasswordResetEvent(modelMapper.map(account, AccountInfo.class), request.getRequestURL().toString(), request.getLocale()));
    }

    public void resetForgottenPasswordNewPassword(String token) {
        PasswordResetVerificationToken passwordResetVerificationToken = passwordResetVerificationTokenService.getPasswordResetVerificationTokenByToken(token);
        passwordResetVerificationTokenService.isPasswordResetVerificationTokenExpired(passwordResetVerificationToken);

    }

    public void resetForgottenPasswordToken(String token, String newPassword) {
        PasswordResetVerificationToken passwordResetVerificationToken = passwordResetVerificationTokenService.getPasswordResetVerificationTokenByToken(token);
        Account account = findAccountById(passwordResetVerificationToken.getAccountId());
        account.setPassword(passwordEncoder.encode(newPassword));
        passwordResetVerificationTokenService.deletePasswordResetVerificationToken(passwordResetVerificationToken);
    }

    private void setAccountVerified(Long accountId) {
        Account account = findAccountById(accountId);
        account.setVerified(true);
        account.setRoles(List.of(UserRole.ROLE_USER));
    }

    private void publishEventSendVerificationEmail(HttpServletRequest request, AccountInfo registered) {
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getRequestURL().toString(), request.getLocale()));
    }


}
