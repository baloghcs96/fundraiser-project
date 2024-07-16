package hu.progmasters.fundraiser.controller;

import hu.progmasters.fundraiser.dto.incoming.AccountChangePassword;
import hu.progmasters.fundraiser.dto.incoming.AccountGetEmailForForgottenPasswordCommand;
import hu.progmasters.fundraiser.dto.incoming.AccountSaveUpdateCommand;
import hu.progmasters.fundraiser.dto.outgoing.AccountInfo;
import hu.progmasters.fundraiser.dto.outgoing.ChooseCurrency;
import hu.progmasters.fundraiser.service.AccountService;
import hu.progmasters.fundraiser.service.SecurityService;
import hu.progmasters.fundraiser.service.WebService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Objects;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/fundraiser/account")
@Slf4j
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SecurityService securityService;
    private final WebService webService;

    @GetMapping("/saveButton")
    @ResponseStatus(OK)
    public ResponseEntity<String> saveButton() {
        return webService.createHtmlContent("accountSave", "Account Register - FUNDRAISER");
    }

    @PostMapping("/saveForm")
    @ResponseStatus(CREATED)
    public ResponseEntity<String> saveForm(@RequestParam String accountName,
                                           @RequestParam String password,
                                           @RequestParam String email,
                                           @RequestParam String currency,
                                           HttpServletRequest request) {
        this.saveAccount(new AccountSaveUpdateCommand(accountName, password, email, currency), request);
        return webService.createHtmlContent("accountSaveCompleted","Account Saved - FUNDRAISER");
    }

    @PostMapping("/save")
    @ResponseStatus(CREATED)
    public AccountInfo saveAccount(@RequestBody @Valid AccountSaveUpdateCommand command, HttpServletRequest request) {
        log.info("Account creation requested, POST /api/fundraiser/account: {}", command.toString());
        return accountService.saveAccount(command, request);
    }

    @GetMapping("/saveForm/registrationConfirm")
    @ResponseStatus(OK)
    public ResponseEntity<String> confirmRegistrationForm(@RequestParam String token) {
        confirmRegistration(token);
        return webService.createHtmlContent("accountSaveConfirm","Account Registration Confirmed - FUNDRAISER");
    }

    @GetMapping("/save/registrationConfirm")
    @ResponseStatus(OK)
    public String confirmRegistration(@RequestParam String token) {
        log.info("Registration confirmation requested, GET /api/fundraiser/account/registrationConfirm?token={}", token);
        accountService.confirmRegistration(token);
        return "Registration confirmed successfully!";
    }

    @PostMapping("/createNewToken")
    @ResponseStatus(CREATED)
    public void createNewToken(HttpServletRequest request) {
        log.info("New token creation requested, POST /api/fundraiser/account/createNewToken");
        Authentication authentication = securityService.getAuthentication();
        accountService.createNewAccountToken(authentication, request);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = securityService.getAuthentication();
        if (auth != null) {
            accountService.logout(request, response, auth);
            return ResponseEntity.ok("Logout successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/loginButton")
    public ResponseEntity<String> loginButton() {
        return webService.createHtmlContent("loginButton","Login - FUNDRAISER");
    }

    @PostMapping("/loginButton")
    public ResponseEntity<String> loginButton(@RequestParam String accountName,
                                              @RequestParam String password) {
        try {
            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(accountName, password);
            Authentication auth = securityService.getAuthenticationManager().authenticate(authReq);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return webService.createHtmlContent("loginSuccessful","Login Successful - FUNDRAISER");
        } catch (AuthenticationException e) {
            return webService.createHtmlContent("loginFail","Login Failed - FUNDRAISER");
        }
    }



    @GetMapping("/login")
    @ResponseStatus(OK)
    public UserDetails getLoggedInUser() {
        Authentication authentication = securityService.getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }

    @GetMapping("/currencies")
    @ResponseStatus(OK)
    public ChooseCurrency getAvailableCurrenciesForNewAccount() {
        log.info("Available currencies requested, GET /api/fundraiser/account/currencies");
        return accountService.getAvailableCurrenciesForNewAccount();
    }

    @GetMapping("/myAccount")
    @ResponseStatus(OK)
    public AccountInfo getMyAccount() {
        Long loggedInUserId = accountService.getLoggedInUserId(securityService.getAuthentication());
        log.info("Account info requested, GET /api/fundraiser/account/myAccount");
        return accountService.getAccountInfo(loggedInUserId);
    }

    @PutMapping("/update")
    @ResponseStatus(OK)
    public AccountInfo updateAccount(@Valid @RequestBody AccountSaveUpdateCommand command, HttpServletRequest request) {
        log.info("Account update requested, PUT /api/fundraiser/account: {}", command.toString());
        Long loggedInUserId = accountService.getLoggedInUserId(securityService.getAuthentication());
        log.info("Account id: {}", loggedInUserId);
        return accountService.updateAccount(loggedInUserId, command, request);
    }

    @PutMapping("/resetPassword")
    @ResponseStatus(OK)
    public void resetPassword(@Valid @RequestBody AccountChangePassword command) {
        Authentication authentication = securityService.getAuthentication();
        accountService.resetPassword(authentication, command);
    }


    @GetMapping("/resetPasswordForm")
    public ResponseEntity<String> resetPasswordForm() {
        return webService.createHtmlContent("resetPassword","Reset Password - FUNDRAISER");
    }

    @PostMapping("/resetPasswordForm")
    public ResponseEntity<String> resetPasswordForm(HttpServletRequest request,
                                                    @RequestParam String email) {
        resetForgottenPassword(new AccountGetEmailForForgottenPasswordCommand(email), request);
        return webService.createHtmlContent("resetPasswordRequested","Reset Password Requested - FUNDRAISER");
    }

    @GetMapping("/resetPasswordForm/newPassword")
    public ResponseEntity<String> resetPasswordFormNewPassword(@RequestParam String token) {
        return resetForgottenPasswordNewPassword(token);
    }

    @PutMapping("/resetForgottenPassword")
    @ResponseStatus(OK)
    public void resetForgottenPassword(@Valid @RequestBody AccountGetEmailForForgottenPasswordCommand command,
                                       HttpServletRequest request) {
        accountService.resetForgottenPassword(request, command);
    }

    @GetMapping("/resetForgottenPassword/newPassword")
    @ResponseStatus(OK)
    public ResponseEntity<String> resetForgottenPasswordNewPassword(@RequestParam String token) {
        accountService.resetForgottenPasswordNewPassword(token);
        ResponseEntity htmlContent = webService.createHtmlContent("resetForgottenPassword","Reset Password - FUNDRAISER");
        return new ResponseEntity<>(Objects.requireNonNull(htmlContent.getBody()).toString().replace("${token}", token), HttpStatus.OK);
    }

    @PostMapping("/resetForgottenPassword/confirm")
    @ResponseStatus(OK)
    public ResponseEntity<String> resetForgottenPasswordToken(@RequestParam String token,
                                                              @RequestParam String newPassword) {
        accountService.resetForgottenPasswordToken(token, newPassword);
        return webService.createHtmlContent("resetPasswordComplete","Password reset completed - FUNDRAISER");
    }

    @GetMapping("/checkAdminRole")
    @ResponseStatus(OK)
    public String getAdminString() {
        return "You are an admin!";
    }

    @GetMapping("/checkUserRole")
    @ResponseStatus(OK)
    public String getUserString() {
        return "You are an user!";
    }

}
