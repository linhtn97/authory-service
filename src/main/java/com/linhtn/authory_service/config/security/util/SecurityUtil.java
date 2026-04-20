package com.linhtn.authory_service.config.security.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.linhtn.authory_service.config.security.model.Principal;

public class SecurityUtil {
    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static Optional<Object> getPrincipal() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal);
    }

    public static Optional<Principal> getAccessPrincipal() {
        return getPrincipal().filter(Principal.class::isInstance).map(Principal.class::cast);
    }
}
