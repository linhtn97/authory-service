package com.linhtn.authory_service.modules.auth.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.config.security.model.Principal;

public interface PartnerAuthenticationUseCase {
    Principal getPrincipal(String clientId, String hashedSecret);

    void issueApiKey(UUID partnerId);
}
