package com.linhtn.authory_service.modules.auth.service;

import com.linhtn.authory_service.config.security.model.PrincipalProfile;
import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.modules.auth.domain.dto.request.LoginRequest;
import com.linhtn.authory_service.modules.auth.domain.dto.request.RegisterRequest;
import com.linhtn.authory_service.modules.auth.domain.model.Auth;

import java.util.UUID;

public interface UserAuthenticationService {
    void register(RegisterRequest request);

    Auth login(LoginRequest request);

    void logout(String token);

    Auth refreshToken(String token);

    PrincipalProfile getPrincipal(String token);

    PrincipalProfile getPrincipal(UUID userId);

    PrincipalAuthorities getAuthorities(UUID userId);
    PrincipalAuthorities getAuthorities(String token);
}
