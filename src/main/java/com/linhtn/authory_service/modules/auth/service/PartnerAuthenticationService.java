package com.linhtn.authory_service.modules.auth.service;

import com.linhtn.authory_service.config.security.model.PrincipalProfile;
import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;

import java.util.UUID;

public interface PartnerAuthenticationService {
    PrincipalProfile getPrincipal(String clientId, String hashedSecret);

    PrincipalAuthorities getAuthorities(UUID partnerId);
}
