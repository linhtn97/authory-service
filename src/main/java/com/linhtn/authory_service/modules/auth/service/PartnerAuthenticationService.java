package com.linhtn.authory_service.modules.auth.service;

import java.util.UUID;

import com.linhtn.authory_service.config.security.model.Principal;

public interface PartnerAuthenticationService {
    Principal getPrincipal(String clientId, String hashedSecret);

    void issueApiKey(UUID partnerId);
}
