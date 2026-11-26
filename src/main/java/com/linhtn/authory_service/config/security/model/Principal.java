package com.linhtn.authory_service.config.security.model;

import java.io.Serializable;
import java.util.UUID;

public record Principal(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        PrincipalAuthorities authorities)
        implements Serializable {
    private static final long serialVersionUID = 1L;
}
