package hu.progmasters.fundraiser.repository.token;

import hu.progmasters.fundraiser.domain.entity.token.PasswordResetVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetVerificationTokenRepository
        extends JpaRepository<PasswordResetVerificationToken, Long> {

    Optional<PasswordResetVerificationToken> findByToken(String token);

    Optional<PasswordResetVerificationToken> findByAccountId(Long accountId);
}