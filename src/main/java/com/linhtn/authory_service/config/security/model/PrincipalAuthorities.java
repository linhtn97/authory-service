package com.linhtn.authory_service.config.security.model;

import java.util.Set;

public record PrincipalAuthorities(
        Set<String> roles, Set<String> groups, Set<String> resources, Set<String> permissions) {}
