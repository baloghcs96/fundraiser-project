package hu.progmasters.fundraiser.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@ResponseStatus(BAD_REQUEST)
@Slf4j
public class GlobalExceptionHandler {

    private static final int MIN_TIME_DIFFERENCE = 10;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationError methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationError validationError = new ValidationError(Objects.requireNonNull(e.getBindingResult().getFieldError()).getField(), e.getBindingResult().getFieldError().getDefaultMessage());
        log.error(validationError.toString());
        return validationError;
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    public ValidationError accountAlreadyVerifiedException(AccountAlreadyVerifiedException e) {
        ValidationError validationError = new ValidationError("email", "Account already verified: " + e.getEmail());
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(AccountAlreadyExistByNameException.class)
    public ValidationError accountAlreadyExistByNameException(AccountAlreadyExistByNameException e) {
        ValidationError validationError = new ValidationError("accountName", "There is an account with that name: " + e.getAccountName());
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(AccountAlreadyExistByEmailException.class)
    public ValidationError accountAlreadyExistByEmailException(AccountAlreadyExistByEmailException e) {
        ValidationError validationError = new ValidationError("email", "There is an account with that email address: " + e.getEmail());
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(AccountVerificationTokenNotFoundByTokenException.class)
    public ValidationError accountVerificationTokenNotFoundByTokenException(AccountVerificationTokenNotFoundByTokenException e) {
        ValidationError validationError = new ValidationError("token", "There is no token with that value: " + e.getToken());
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(AccountVerificationTokenNotFoundByAccountIdException.class)
    public ValidationError accountVerificationTokenNotFoundByAccountIdException(AccountVerificationTokenNotFoundByAccountIdException e) {
        ValidationError validationError = new ValidationError("accountId", "There is no token for the account: " + e.getAccountId());
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(AccountVerificationTokenExpiredException.class)
    public ValidationError accountVerificationTokenExpiredException(AccountVerificationTokenExpiredException e) {
        ValidationError validationError = new ValidationError("token", "The token has expired.");
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(TransactionVerificationTokenNotFoundByTokenException.class)
    public ValidationError transactionVerificationTokenNotFoundByTokenException(TransactionVerificationTokenNotFoundByTokenException e) {
        ValidationError validationError = new ValidationError("token", "There is no token with that value: " + e.getToken());
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(TransactionVerificationTokenExpiredException.class)
    public ValidationError transactionVerificationTokenExpiredException(TransactionVerificationTokenExpiredException e) {
        ValidationError validationError = new ValidationError("token", "The token has expired.");
        log.error(e.getMessage());
        return validationError;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(AccountNotFoundByEmailException.class)
    public ValidationError accountNotFoundByEmailException(AccountNotFoundByEmailException e) {
        ValidationError validationError = new ValidationError("email", "no account found with email: " + e.getEmail());
        log.error(validationError.toString());
        return validationError;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(AccountNotFoundByIdException.class)
    public ValidationError accountNotFoundByIdException(AccountNotFoundByIdException e) {
        ValidationError validationError = new ValidationError("accountId", "no account found with id: " + e.getId());
        log.error(validationError.toString());
        return validationError;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(AccountNotFoundByNameException.class)
    public ValidationError accountNotFoundByNameException(AccountNotFoundByNameException e) {
        ValidationError validationError = new ValidationError("accountName", "no account found with name: " + e.getName());
        log.error(validationError.toString());
        return validationError;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(FundNotFoundByTitleException.class)
    public ValidationError fundNotFoundByTitleException(FundNotFoundByTitleException e) {
        ValidationError validationError = new ValidationError("title", "no fund found with title: " + e.getTitle());
        log.error(validationError.toString());
        return validationError;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(FundNotFoundByCategoryException.class)
    public ValidationError fundNotFoundByCategoryException(FundNotFoundByCategoryException e) {
        ValidationError validationError = new ValidationError("category", "no fund found with category: " + e.getCategory().toString());
        log.error(validationError.toString());
        return validationError;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(FundIsClosedException.class)
    public ValidationError fundIsClosedException(FundIsClosedException e) {
        ValidationError validationError = new ValidationError("fundId", "The fund is closed: " + e.getMessage());
        log.error(validationError.toString());
        return validationError;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(DateTooFarException.class)
    public ValidationError dateTooFarException(DateTooFarException e) {
        ValidationError validationError = new ValidationError("date", "The close date (" + e.getMessage() + " months) is more than one year away.");
        log.error(validationError.toString());
        return validationError;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotEnoughBalanceToTransferException.class)
    public ValidationError notEnoughBalanceToTransferException(NotEnoughBalanceToTransferException e) {
        ValidationError validationError = new ValidationError("balance", "not enough balance to transfer");
        log.error(validationError.toString());
        return validationError;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(FundNotFoundByIdException.class)
    public ValidationError fundNotFoundByIdException(FundNotFoundByIdException e) {
        ValidationError validationError = new ValidationError("fundId", "no fund found with id: " + e.getId());
        log.error(validationError.toString());
        return validationError;
    }

    @ExceptionHandler(InvalidScheduleTimeException.class)
    public ValidationError invalidScheduleTimeException(InvalidScheduleTimeException e) {
        String errorMessage;
        if (e.getScheduleTime().isBefore(LocalDateTime.now())) {
            errorMessage = "The date must be in the future: " + e.getScheduleTime();
        } else if (ChronoUnit.MINUTES.between(LocalDateTime.now(), e.getScheduleTime()) < MIN_TIME_DIFFERENCE) {
            errorMessage = "The difference between the transaction time and the current time should be more than" + MIN_TIME_DIFFERENCE + "minutes";
        } else {
            errorMessage = "Invalid schedule time.";
        }
        ValidationError validationError = new ValidationError("transactionTime", errorMessage);
        log.error(validationError.toString());
        return validationError;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ValidationError httpMessageNotReadableException(HttpMessageNotReadableException e) {
        ValidationError validationError = new ValidationError("request", e.getCause().getMessage());
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(AlreadyLoggedInException.class)
    public ValidationError alreadyLoggedInException(AlreadyLoggedInException e) {
        ValidationError validationError = new ValidationError("accountName", "Already logged in: " + e.getAccountName());
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(PasswordNotDifferentException.class)
    public ValidationError passwordNotDifferentException(PasswordNotDifferentException e) {
        ValidationError validationError = new ValidationError("password", "Password need to be different.");
        log.error(e.getMessage());
        return validationError;
    }

    @ExceptionHandler(TransactionToYourselfException.class)
    public ValidationError handleTransactionToYourselfException(TransactionToYourselfException e) {
        ValidationError validationError = new ValidationError("accountId", "You cannot transfer money to yourself: " + e.getAccountId());
        log.error(validationError.toString());
        return validationError;
    }

    @ExceptionHandler(NotEnoughCoinToPurchaseException.class)
    public ValidationError handleNotEnoughCoinException(NotEnoughCoinToPurchaseException e) {
        ValidationError validationError = new ValidationError("coin", "Not enough coin to purchase: " + e.getAccountCoin());
        log.error(validationError.toString());
        return validationError;
    }

    @ExceptionHandler(FlashSaleItemNotFoundException.class)
    public ValidationError handleFlashSaleItemNotFoundException(FlashSaleItemNotFoundException e) {
        ValidationError validationError = new ValidationError("flashSaleItemId", "No flash sale item found with id: " + e.getItemId());
        log.error(validationError.toString());
        return validationError;
    }

    @ExceptionHandler(GradeNotFoundException.class)
    public ValidationError handleGradeNotFoundException(GradeNotFoundException e) {
        ValidationError validationError = new ValidationError("grade", "No grade found with: " + e.getGrade());
        log.error(validationError.toString());
        return validationError;
    }

    @ExceptionHandler(BadgeAlreadyExistException.class)
    public ValidationError handleBadgeAlreadyExistException(BadgeAlreadyExistException e) {
        ValidationError validationError = new ValidationError("grade", "Badge already exist with grade: " + e.getGrade());
        log.error(validationError.toString());
        return validationError;
    }

}
