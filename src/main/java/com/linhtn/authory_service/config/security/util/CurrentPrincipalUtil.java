package com.linhtn.authory_service.config.security.util;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.linhtn.security_core.model.AuthenticatedUser;

public class CurrentPrincipalUtil {
    private CurrentPrincipalUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<AuthenticatedUser> getPrincipal() {
        Optional<AuthenticatedUser> springUser =
                Optional.ofNullable(SecurityContextHolder.getContext())
                        .map(SecurityContext::getAuthentication)
                        .map(Authentication::getPrincipal)
                        .filter(AuthenticatedUser.class::isInstance)
                        .map(AuthenticatedUser.class::cast);

        if (springUser.isPresent()) {
            return springUser;
        }

        return com.linhtn.security_core.context.SecurityContextHolder.getUser();
    }

    public static UUID getId() {
        return getPrincipal()
                .map(AuthenticatedUser::getUserId)
                .map(
                        idStr -> {
                            try {
                                return UUID.fromString(idStr);
                            } catch (IllegalArgumentException e) {
                                return null;
                            }
                        })
                .orElse(null);
    }

    public static String getUsername() {
        return getPrincipal().map(AuthenticatedUser::getUsername).orElse(null);
    }

    public static String getEmail() {
        return getPrincipal().map(AuthenticatedUser::getEmail).orElse(null);
    }

    public static boolean isAuthenticated() {
        return getPrincipal().map(AuthenticatedUser::isAuthenticated).orElse(false);
    }
}
