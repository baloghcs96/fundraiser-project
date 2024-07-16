package hu.progmasters.fundraiser.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base")
    private String baseCurrency;

    @Column(name = "requestTime")
    private LocalDateTime localDateTime;

    @Column(name = "USD")
    private Double usdCurrency;

    @Column(name = "HUF")
    private Double hufCurrency;

    @Override
    public String toString() {
        return "Exchange{" +
                "id=" + id +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", localDateTime=" + localDateTime +
                ", usdCurrency=" + usdCurrency +
                ", hufCurrency=" + hufCurrency +
                '}';
    }
}
