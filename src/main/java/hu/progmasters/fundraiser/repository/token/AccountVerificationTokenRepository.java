package hu.progmasters.fundraiser.repository.token;

import hu.progmasters.fundraiser.domain.entity.token.AccountVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountVerificationTokenRepository
        extends JpaRepository<AccountVerificationToken, Long> {

    Optional<AccountVerificationToken> findByToken(String token);

    Optional<AccountVerificationToken> findByAccountId(Long accountId);
}