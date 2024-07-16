package hu.progmasters.fundraiser.domain.entity;

import hu.progmasters.fundraiser.domain.enumeration.Currency;
import hu.progmasters.fundraiser.domain.enumeration.Grade;
import hu.progmasters.fundraiser.domain.enumeration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(unique = true)
    private String accountName;

    private String password;

    @Column(unique = true)
    private String email;

    private double balance;

    private int coin;

    private int experiencePoints;

    private boolean verified;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.BRONZE;

    @OneToMany(mappedBy = "account")
    private List<Fund> funds;

    @OneToMany(mappedBy = "senderAccount")
    private List<Transaction> accountTransactions;

    @OneToMany(mappedBy = "account")
    private List<Purchase> purchases;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role")
    private List<UserRole> roles;


    public List<GrantedAuthority> getAuthorities() {
        return Stream.concat(
                roles.stream().map(role -> new SimpleGrantedAuthority(role.name())),
                Stream.of(new SimpleGrantedAuthority(grade.getName()))
        ).collect(Collectors.toList());
    }

    public void setAuthorities(String roleUser) {
        roles = List.of(UserRole.valueOf(roleUser));
    }
}