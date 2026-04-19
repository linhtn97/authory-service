package com.linhtn.authory_service.modules.rbac.service;

import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;

import java.util.UUID;

public interface PartnerAuthoryService {
    PrincipalAuthorities getAuthorities(UUID partnerId);
}
