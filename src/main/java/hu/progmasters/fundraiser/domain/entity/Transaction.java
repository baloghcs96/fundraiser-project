package hu.progmasters.fundraiser.domain.entity;

import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.domain.enumeration.TransactionState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "sender_account_id")
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "target_fund_id")
    private Fund targetFund;

    private double sentAmount;

    private double receivedAmount;

    @Enumerated(EnumType.STRING)
    private Currency sentCurrency;

    @Enumerated(EnumType.STRING)
    private Currency receivedCurrency;

    @Enumerated(EnumType.STRING)
    private TransactionState transactionState;

    private LocalDateTime transactionTime;

    private int attemptCount;

}
