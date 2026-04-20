package com.linhtn.authory_service.modules.auth.service;

import java.util.UUID;

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.modules.auth.domain.dto.request.LoginRequest;
import com.linhtn.authory_service.modules.auth.domain.dto.request.RegisterRequest;
import com.linhtn.authory_service.modules.auth.domain.model.Auth;

public interface UserAuthenticationService {
    void register(RegisterRequest request);

    Auth login(LoginRequest request);

    void logout(String token);

    Auth refreshToken(String token);

    Principal getPrincipal(String token);

    Principal getPrincipal(UUID userId);
}
