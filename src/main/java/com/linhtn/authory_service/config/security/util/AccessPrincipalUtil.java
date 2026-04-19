package com.linhtn.authory_service.config.security.util;

import com.linhtn.authory_service.config.security.model.PrincipalProfile;

import java.util.Optional;
import java.util.UUID;

public class AccessPrincipalUtil {
    private AccessPrincipalUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static Optional<PrincipalProfile> getPrincipal() {
        return SecurityUtil.getAccessPrincipal();
    }

    public static UUID getId() {
        return getPrincipal().map(PrincipalProfile::id).orElse(null);
    }

    public static String getUsername() {
        return getPrincipal().map(PrincipalProfile::username).orElse(null);
    }

    public static String getEmail() {
        return getPrincipal().map(PrincipalProfile::email).orElse(null);
    }

}
