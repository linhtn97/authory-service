package com.linhtn.authory_service.config.security.util;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.config.security.model.Principal;

public class AccessPrincipalUtil {
    private AccessPrincipalUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static Optional<Principal> getPrincipal() {
        return SecurityUtil.getAccessPrincipal();
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
}
