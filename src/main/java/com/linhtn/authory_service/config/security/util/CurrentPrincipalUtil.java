package com.linhtn.authory_service.config.security.util;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;

public class CurrentPrincipalUtil {
    private CurrentPrincipalUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<Principal> getPrincipal() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(Principal.class::isInstance)
                .map(Principal.class::cast);
    }

    public static UUID getId() {
        return getPrincipal().map(Principal::id).orElse(null);
    }

    public static String getUsername() {
        return getPrincipal().map(Principal::username).orElse(null);
    }

    public static String getEmail() {
        return getPrincipal().map(Principal::email).orElse(null);
    }

    public static boolean isAuthenticated() {
        return getPrincipal().isPresent();
    }

    public static Optional<PrincipalAuthorities> getAuthorities() {
        return getPrincipal().map(Principal::authorities);
    }
}
