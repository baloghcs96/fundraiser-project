package hu.progmasters.fundraiser.repository.token;

import hu.progmasters.fundraiser.domain.entity.token.TransactionVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionVerificationTokenRepository
        extends JpaRepository<TransactionVerificationToken, Long> {

    Optional<TransactionVerificationToken> findByToken(String token);

    Optional<TransactionVerificationToken> findByTransactionId(Long accountId);
}