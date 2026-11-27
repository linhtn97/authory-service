package com.linhtn.authory_service.modules.rbac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;

public interface PartnerAuthorityUseCase {
    PrincipalAuthorities getAuthorities(UUID partnerId);
}
