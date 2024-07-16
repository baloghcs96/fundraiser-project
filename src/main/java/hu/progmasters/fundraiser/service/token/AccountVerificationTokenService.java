package hu.progmasters.fundraiser.service.token;

import hu.progmasters.fundraiser.domain.entity.token.AccountVerificationToken;
import hu.progmasters.fundraiser.exception.AccountVerificationTokenExpiredException;
import hu.progmasters.fundraiser.exception.AccountVerificationTokenNotFoundByTokenException;
import hu.progmasters.fundraiser.repository.token.AccountVerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class AccountVerificationTokenService {

    private AccountVerificationTokenRepository accountVerificationTokenRepository;

    public String createAccountVerificationToken(Long accountId) {
        AccountVerificationToken checkToken = getAccountVerificationTokenByAccountId(accountId);
        if (checkToken != null) {
            accountVerificationTokenRepository.delete(checkToken);
        }
        AccountVerificationToken myToken = new AccountVerificationToken(accountId);
        accountVerificationTokenRepository.save(myToken);
        return myToken.getToken();
    }

    public AccountVerificationToken getAccountVerificationTokenByToken(String token) {
        return accountVerificationTokenRepository.findByToken(token).orElseThrow(() -> new AccountVerificationTokenNotFoundByTokenException(token));
    }

    private AccountVerificationToken getAccountVerificationTokenByAccountId(Long accountId) {
        return accountVerificationTokenRepository.findByAccountId(accountId).orElse(null);
    }

    public void isAccountTokenExpired(AccountVerificationToken token) {
        Calendar cal = Calendar.getInstance();
        boolean isExpired = token.getExpiryDate().getTime() - cal.getTime().getTime() <= 0;
        if (isExpired) {
            accountVerificationTokenRepository.delete(token);
            throw new AccountVerificationTokenExpiredException(token.getToken());
        }
    }

    public void deleteAccountToken(AccountVerificationToken accountVerificationToken) {
        accountVerificationTokenRepository.delete(accountVerificationToken);
    }

}
