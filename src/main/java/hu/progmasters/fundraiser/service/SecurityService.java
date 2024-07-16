package hu.progmasters.fundraiser.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Getter
@AllArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}