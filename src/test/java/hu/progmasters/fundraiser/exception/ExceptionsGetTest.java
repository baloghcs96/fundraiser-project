package hu.progmasters.fundraiser.exception;

import hu.progmasters.fundraiser.domain.enumeration.Category;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionsGetTest {


    @Test
    void testAccountAlreadyExistByEmailException() {
        String email = "test@example.com";
        AccountAlreadyExistByEmailException exception = new AccountAlreadyExistByEmailException(email);
        assertEquals(email, exception.getEmail());
    }

    @Test
    void testAccountAlreadyExistByNameException() {
        String accountName = "testName";
        AccountAlreadyExistByNameException exception = new AccountAlreadyExistByNameException(accountName);
        assertEquals(accountName, exception.getAccountName());
    }

    @Test
    void testAccountAlreadyVerifiedException() {
        String email = "verified@example.com";
        AccountAlreadyVerifiedException exception = new AccountAlreadyVerifiedException(email);
        assertEquals(email, exception.getEmail());
    }

    @Test
    void testAccountNotFoundByEmailException() {
        String email = "notfound@example.com";
        AccountNotFoundByEmailException exception = new AccountNotFoundByEmailException(email);
        assertEquals(email, exception.getEmail());
    }

    @Test
    void testAccountNotFoundByIdException() {
        Long id = 1L;
        AccountNotFoundByIdException exception = new AccountNotFoundByIdException(id);
        assertEquals(id, exception.getId());
    }

    @Test
    void testAccountNotFoundByNameException() {
        String name = "notFoundName";
        AccountNotFoundByNameException exception = new AccountNotFoundByNameException(name);
        assertEquals(name, exception.getName());
    }

    @Test
    void testAccountVerificationTokenExpiredException() {
        String token = "expiredToken";
        AccountVerificationTokenExpiredException exception = new AccountVerificationTokenExpiredException(token);
        assertEquals(token, exception.getToken());
    }

    @Test
    void testAccountVerificationTokenNotFoundByAccountIdException() {
        Long accountId = 2L;
        AccountVerificationTokenNotFoundByAccountIdException exception = new AccountVerificationTokenNotFoundByAccountIdException(accountId);
        assertEquals(accountId, exception.getAccountId());
    }

    @Test
    void testAccountVerificationTokenNotFoundByTokenException() {
        String token = "notFoundToken";
        AccountVerificationTokenNotFoundByTokenException exception = new AccountVerificationTokenNotFoundByTokenException(token);
        assertEquals(token, exception.getToken());
    }

    @Test
    void testAlreadyLoggedInException() {
        String accountName = "loggedInName";
        AlreadyLoggedInException exception = new AlreadyLoggedInException(accountName);
        assertEquals(accountName, exception.getAccountName());
    }

    @Test
    void testBadgeAlreadyExistException() {
        Grade grade = Grade.GOLD;
        BadgeAlreadyExistException exception = new BadgeAlreadyExistException(grade);
        assertEquals(grade, exception.getGrade());
    }

    @Test
    void testCloudinaryException() {
        String message = "Cloudinary error";
        CloudinaryException exception = new CloudinaryException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testDateTooFarException() {
        int monthBetween = 13;
        DateTooFarException exception = new DateTooFarException(monthBetween);
        assertEquals(monthBetween, exception.getMonthBetween());
    }

    @Test
    void testFlashSaleItemNotFoundException() {
        Long itemId = 3L;
        FlashSaleItemNotFoundException exception = new FlashSaleItemNotFoundException(itemId);
        assertEquals(itemId, exception.getItemId());
    }

    @Test
    void testFundNotFoundByCategoryExceptionGetter() {
        Category expectedCategory = Category.ART;
        FundNotFoundByCategoryException exception = new FundNotFoundByCategoryException(expectedCategory);
        assertEquals(expectedCategory, exception.getCategory());
    }

    @Test
    void testFundNotFoundByIdExceptionGetter() {
        Long expectedId = 1L;
        FundNotFoundByIdException exception = new FundNotFoundByIdException(expectedId);
        assertEquals(expectedId, exception.getId());
    }

    @Test
    void testFundNotFoundByTitleExceptionGetter() {
        String expectedTitle = "Missing Fund";
        FundNotFoundByTitleException exception = new FundNotFoundByTitleException(expectedTitle);
        assertEquals(expectedTitle, exception.getTitle());
    }

    @Test
    void testGradeNotFoundExceptionGetter() {
        Grade expectedGrade = Grade.GOLD;
        GradeNotFoundException exception = new GradeNotFoundException(expectedGrade);
        assertEquals(expectedGrade, exception.getGrade());
    }

    @Test
    void testInvalidScheduleTimeExceptionGetter() {
        LocalDateTime expectedScheduleTime = LocalDateTime.now().plusDays(1);
        InvalidScheduleTimeException exception = new InvalidScheduleTimeException(expectedScheduleTime);
        assertEquals(expectedScheduleTime, exception.getScheduleTime());
    }

    @Test
    void testFundIsClosedExceptionGetter() {
        String expectedFundId = "fund123";
        FundIsClosedException exception = new FundIsClosedException(expectedFundId);
        assertEquals(expectedFundId, exception.getFundId());
    }

    @Test
    void testPasswordNotDifferentExceptionGetter() {
        PasswordNotDifferentException exception = new PasswordNotDifferentException();
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testTransactionVerificationTokenNotFoundByTokenExceptionGetter() {
        String expectedToken = "token123";
        TransactionVerificationTokenNotFoundByTokenException exception = new TransactionVerificationTokenNotFoundByTokenException(expectedToken);
        assertEquals(expectedToken, exception.getToken());
    }

    @Test
    void testTransactionVerificationTokenExpiredExceptionGetter() {
        String expectedToken = "token123";
        TransactionVerificationTokenExpiredException exception = new TransactionVerificationTokenExpiredException(expectedToken);
        assertEquals(expectedToken, exception.getToken());
    }
}