package hu.progmasters.fundraiser.service.token;

import hu.progmasters.fundraiser.domain.entity.token.PasswordResetVerificationToken;
import hu.progmasters.fundraiser.exception.AccountVerificationTokenExpiredException;
import hu.progmasters.fundraiser.exception.AccountVerificationTokenNotFoundByTokenException;
import hu.progmasters.fundraiser.repository.token.PasswordResetVerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class PasswordResetVerificationTokenService {

    private PasswordResetVerificationTokenRepository passwordResetVerificationTokenRepository;

    public String createPasswordResetVerificationToken(Long accountId) {
        PasswordResetVerificationToken checkToken = getPasswordResetVerificationTokenByAccountId(accountId);
        if (checkToken != null) {
            passwordResetVerificationTokenRepository.delete(checkToken);
        }
        PasswordResetVerificationToken myToken = new PasswordResetVerificationToken(accountId);
        passwordResetVerificationTokenRepository.save(myToken);
        return myToken.getToken();
    }

    public PasswordResetVerificationToken getPasswordResetVerificationTokenByToken(String token) {
        return passwordResetVerificationTokenRepository.findByToken(token).orElseThrow(() -> new AccountVerificationTokenNotFoundByTokenException(token));
    }

    private PasswordResetVerificationToken getPasswordResetVerificationTokenByAccountId(Long accountId) {
        return passwordResetVerificationTokenRepository.findByAccountId(accountId).orElse(null);
    }

    public void isPasswordResetVerificationTokenExpired(PasswordResetVerificationToken token) {
        Calendar cal = Calendar.getInstance();
        boolean isExpired = token.getExpiryDate().getTime() - cal.getTime().getTime() <= 0;
        if (isExpired) {
            passwordResetVerificationTokenRepository.delete(token);
            throw new AccountVerificationTokenExpiredException(token.getToken());
        }
    }

    public void deletePasswordResetVerificationToken(PasswordResetVerificationToken passwordResetVerificationToken) {
        passwordResetVerificationTokenRepository.delete(passwordResetVerificationToken);
    }

}
