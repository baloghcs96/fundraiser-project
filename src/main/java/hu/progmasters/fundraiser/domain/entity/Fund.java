package hu.progmasters.fundraiser.domain.entity;

import hu.progmasters.fundraiser.domain.enumeration.Category;
import hu.progmasters.fundraiser.domain.enumeration.Currency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Fund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long fundId;

    private String title;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Double goalAmount;

    private double currentAmount;

    private LocalDateTime completedDate;

    private boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "targetFund")
    private List<Transaction> fundTransactions;

    @OneToMany(mappedBy = "fund")
    private List<Image> images;

    @OneToOne(mappedBy = "fund")
    private Income income;

    private Boolean openForDonation = true;

    private LocalDateTime closeDate;
}
