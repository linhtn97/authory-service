package com.linhtn.authory_service.modules.auth.service;

import com.linhtn.authory_service.config.security.model.Principal;

public interface PartnerAuthenticationService {
    Principal getPrincipal(String clientId, String hashedSecret);
}
