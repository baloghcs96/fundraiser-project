package hu.progmasters.fundraiser.service.token;

import hu.progmasters.fundraiser.domain.entity.token.TransactionVerificationToken;
import hu.progmasters.fundraiser.exception.TransactionVerificationTokenExpiredException;
import hu.progmasters.fundraiser.exception.TransactionVerificationTokenNotFoundByTokenException;
import hu.progmasters.fundraiser.repository.token.TransactionVerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class TransactionVerificationTokenService {

    private TransactionVerificationTokenRepository transactionVerificationTokenRepository;

    public String createTransactionVerificationToken(Long transactionId) {
        TransactionVerificationToken checkToken = getTransactionVerificationTokenByTransactionId(transactionId);
        if (checkToken != null) {
            transactionVerificationTokenRepository.delete(checkToken);
        }
        TransactionVerificationToken myToken = new TransactionVerificationToken(transactionId);
        transactionVerificationTokenRepository.save(myToken);
        return myToken.getToken();
    }

    public TransactionVerificationToken getTransactionVerificationTokenByToken(String token) {
        return transactionVerificationTokenRepository.findByToken(token).orElseThrow(() -> new TransactionVerificationTokenNotFoundByTokenException(token));
    }

    private TransactionVerificationToken getTransactionVerificationTokenByTransactionId(Long transactionId) {
        return transactionVerificationTokenRepository.findByTransactionId(transactionId).orElse(null);
    }

    public void isTransactionTokenExpired(TransactionVerificationToken token) {
        Calendar cal = Calendar.getInstance();
        boolean isExpired = token.getExpiryDate().getTime() - cal.getTime().getTime() <= 0;
        if (isExpired) {
            transactionVerificationTokenRepository.delete(token);
            throw new TransactionVerificationTokenExpiredException(token.getToken());
        }
    }

    public void deleteTransactionToken(TransactionVerificationToken transactionVerificationToken) {
        transactionVerificationTokenRepository.delete(transactionVerificationToken);
    }
}
