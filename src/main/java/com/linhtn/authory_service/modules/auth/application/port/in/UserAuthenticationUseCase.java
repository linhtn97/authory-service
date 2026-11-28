package com.linhtn.authory_service.modules.auth.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.auth.application.dto.command.UserLoginCommand;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserRegisterCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.AuthLoginResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.MfaSetupResult;
import com.linhtn.security_core.model.AuthenticatedUser;

public interface UserAuthenticationUseCase {
    void register(UserRegisterCommand command);

    AuthLoginResult login(UserLoginCommand command);

    void logout(String token);

    AuthLoginResult refreshToken(String token);

    AuthenticatedUser getPrincipal(String token);

    AuthenticatedUser getPrincipal(UUID userId);

    AuthenticatedUser getPrincipalByApiKey(String apiKey);

    AuthenticatedUser getPrincipalByClientCredentials(String clientId, String clientKey);

    ApiCredentialResult issueApiKey(UUID userId);

    MfaSetupResult setupMfa(UUID userId);

    void enableMfa(UUID userId, String code);

    void disableMfa(UUID userId, String password);

    AuthLoginResult verifyMfa(String mfaToken, String code, String ipAddress, String userAgent);

    void requestPasswordReset(String email);

    void resetPasswordWithToken(String token, String newPassword);

    void changePassword(UUID userId, String currentPassword, String newPassword);
}
