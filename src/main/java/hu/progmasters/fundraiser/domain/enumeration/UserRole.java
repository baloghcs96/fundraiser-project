package hu.progmasters.fundraiser.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {

    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    ROLE_UNVERIFIED("UNVERIFIED");

    private final String role;

}
