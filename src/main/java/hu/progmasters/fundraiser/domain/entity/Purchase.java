package hu.progmasters.fundraiser.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String item;

    private int priceInCoin;

    private int quantity;

    private LocalDateTime purchaseTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
