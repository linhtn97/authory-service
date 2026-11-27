package com.linhtn.authory_service.modules.auth.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserLoginCommand;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserRegisterCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.AuthLoginResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.MfaSetupResult;

public interface UserAuthenticationUseCase {
    void register(UserRegisterCommand command);

    AuthLoginResult login(UserLoginCommand command);

    void logout(String token);

    AuthLoginResult refreshToken(String token);

    Principal getPrincipal(String token);

    Principal getPrincipal(UUID userId);

    MfaSetupResult setupMfa(UUID userId);

    void enableMfa(UUID userId, String code);

    void disableMfa(UUID userId, String password);

    AuthLoginResult verifyMfa(String mfaToken, String code, String ipAddress, String userAgent);

    void requestPasswordReset(String email);

    void resetPasswordWithToken(String token, String newPassword);

    void changePassword(UUID userId, String currentPassword, String newPassword);
}
