package com.linhtn.authory_service.modules.auth.representation.rest.mapper;

import com.linhtn.authory_service.modules.auth.application.dto.command.UserLoginCommand;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserRegisterCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.AuthLoginResult;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.LoginRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.RegisterRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.AuthLoginResponse;

public class AuthRestMapper {
    private AuthRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static UserRegisterCommand toCommand(RegisterRequest request) {
        if (request == null) return null;
        return UserRegisterCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    public static UserLoginCommand toCommand(LoginRequest request) {
        if (request == null) return null;
        return UserLoginCommand.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }

    public static AuthLoginResponse toResponse(AuthLoginResult result) {
        if (result == null) return null;
        return AuthLoginResponse.builder()
                .accessToken(result.getAccessToken())
                .expireAccessAt(result.getExpireAccessAt())
                .refreshToken(result.getRefreshToken())
                .expireRefreshAt(result.getExpireRefreshAt())
                .mfaRequired(result.getMfaRequired())
                .mfaToken(result.getMfaToken())
                .build();
    }

    public static com.linhtn.authory_service.modules.auth.representation.rest.dto.response
                    .MfaSetupResponse
            toMfaSetupResponse(
                    com.linhtn.authory_service.modules.auth.application.dto.result.MfaSetupResult
                            result) {
        if (result == null) return null;
        return com.linhtn.authory_service.modules.auth.representation.rest.dto.response
                .MfaSetupResponse.builder()
                .secretKey(result.getSecretKey())
                .qrCodeUri(result.getQrCodeUri())
                .build();
    }
}
