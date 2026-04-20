package com.linhtn.authory_service.modules.rbac.service;

import java.util.UUID;

import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;

public interface UserAuthoryService {
    PrincipalAuthorities getAuthorities(UUID userId);
}
