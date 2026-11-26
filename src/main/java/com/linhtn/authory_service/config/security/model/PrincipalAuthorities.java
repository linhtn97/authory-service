package com.linhtn.authory_service.config.security.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public record PrincipalAuthorities(
        Set<String> roles, Set<String> groups, Set<String> resources, Set<String> permissions)
        implements Serializable {
    private static final long serialVersionUID = 1L;

    public PrincipalAuthorities {
        roles = roles != null ? Set.copyOf(roles) : Collections.emptySet();
        groups = groups != null ? Set.copyOf(groups) : Collections.emptySet();
        resources = resources != null ? Set.copyOf(resources) : Collections.emptySet();
        permissions = permissions != null ? Set.copyOf(permissions) : Collections.emptySet();
    }
}
