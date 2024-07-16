package hu.progmasters.fundraiser.domain.entity.token;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TransactionVerificationToken extends TokenCreateFunction {
    private static final int EXPIRATION_MINUTE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Long transactionId;
    private Date expiryDate;

    public TransactionVerificationToken(Long transactionId) {
        this.token = UUID.randomUUID().toString();
        this.transactionId = transactionId;
        this.expiryDate = calculateExpiryDate(EXPIRATION_MINUTE);
    }
}