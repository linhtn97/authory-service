package com.linhtn.authory_service.config.security.util;

import com.linhtn.authory_service.config.security.model.PrincipalProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtil {
    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static Optional<Object> getPrincipal() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal);
    }

    public static Optional<PrincipalProfile> getAccessPrincipal() {
        return getPrincipal().filter(PrincipalProfile.class::isInstance).map(PrincipalProfile.class::cast);
    }
}
