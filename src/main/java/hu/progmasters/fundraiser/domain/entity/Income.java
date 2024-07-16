package hu.progmasters.fundraiser.domain.entity;

import hu.progmasters.fundraiser.domain.enumeration.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private LocalDateTime confirmTime;

    @OneToOne
    @JoinColumn(name = "fund_id")
    private Fund fund;


}
