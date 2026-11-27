package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.config.security.util.CurrentPrincipalUtil;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserLoginCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.MfaSetupResult;
import com.linhtn.authory_service.modules.auth.application.port.in.PartnerAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.ChangePasswordRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.ForgotPasswordRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.LoginRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.MfaDisableRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.MfaEnableRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.MfaVerifyRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.RefreshTokenRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.RegisterRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.ResetPasswordRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.AuthLoginResponse;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.MfaSetupResponse;
import com.linhtn.authory_service.modules.auth.representation.rest.mapper.AuthRestMapper;
import com.linhtn.idempotency_core.annotation.Idempotent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Authentication & Sessions",
        description = "User login, registration, MFA/2FA, password reset, and principal resolution")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserAuthenticationUseCase userAuthenticationUseCase;
    private final PartnerAuthenticationUseCase partnerAuthenticationUseCase;

    public AuthenticationController(
            UserAuthenticationUseCase userAuthenticationUseCase,
            PartnerAuthenticationUseCase partnerAuthenticationUseCase) {
        this.userAuthenticationUseCase = userAuthenticationUseCase;
        this.partnerAuthenticationUseCase = partnerAuthenticationUseCase;
    }

    @Operation(
            summary = "Register new user account",
            description =
                    "Registers a new user and creates their hashed credentials. Rate-limited.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Registration successful"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded (60 req/min)")
    })
    @PostMapping("/register")
    @Idempotent
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        userAuthenticationUseCase.register(AuthRestMapper.toCommand(request));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "User login with credentials",
            description =
                    "Authenticates with username and password. Supports brute-force lockout after 5 consecutive failed attempts. "
                            + "If MFA is enabled, returns temporary mfaToken.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account locked for 15 minutes"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded (60 req/min)")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(
            @Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        UserLoginCommand command = AuthRestMapper.toCommand(request);
        command.setIpAddress(getClientIp(httpRequest));
        command.setUserAgent(httpRequest.getHeader("User-Agent"));

        var result = userAuthenticationUseCase.login(command);
        return ResponseEntity.ok(AuthRestMapper.toResponse(result));
    }

    @Operation(
            summary = "Verify 2FA TOTP code",
            description =
                    "Completes 2-step verification using the temporary mfaToken and 6-digit TOTP code.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "MFA verified, tokens issued"),
        @ApiResponse(
                responseCode = "401",
                description = "Invalid or expired MFA token or TOTP code")
    })
    @PostMapping("/mfa/verify")
    public ResponseEntity<AuthLoginResponse> verifyMfa(
            @Valid @RequestBody MfaVerifyRequest request, HttpServletRequest httpRequest) {
        var result =
                userAuthenticationUseCase.verifyMfa(
                        request.getMfaToken(),
                        request.getCode(),
                        getClientIp(httpRequest),
                        httpRequest.getHeader("User-Agent"));
        return ResponseEntity.ok(AuthRestMapper.toResponse(result));
    }

    @Operation(
            summary = "Initiate MFA / 2FA setup",
            description =
                    "Generates a new RFC 6238 TOTP secret key and QR code URI for authenticator apps.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "MFA secret and QR code generated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/mfa/setup")
    public ResponseEntity<MfaSetupResponse> setupMfa() {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        MfaSetupResult result = userAuthenticationUseCase.setupMfa(currentUserId);
        return ResponseEntity.ok(
                MfaSetupResponse.builder()
                        .secretKey(result.getSecretKey())
                        .qrCodeUri(result.getQrCodeUri())
                        .build());
    }

    @Operation(
            summary = "Verify initial TOTP and enable 2FA",
            description =
                    "Validates the initial 6-digit code against the generated secret and permanently activates 2FA.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "MFA successfully enabled"),
        @ApiResponse(responseCode = "400", description = "Invalid verification code")
    })
    @PostMapping("/mfa/enable")
    public ResponseEntity<Void> enableMfa(@Valid @RequestBody MfaEnableRequest request) {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        userAuthenticationUseCase.enableMfa(currentUserId, request.getCode());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Disable 2FA",
            description =
                    "Disables 2FA on the authenticated user account with password confirmation.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "MFA successfully disabled"),
        @ApiResponse(responseCode = "401", description = "Password incorrect")
    })
    @PostMapping("/mfa/disable")
    public ResponseEntity<Void> disableMfa(@Valid @RequestBody MfaDisableRequest request) {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        userAuthenticationUseCase.disableMfa(currentUserId, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Request password reset",
            description =
                    "Generates a time-limited 15-minute password reset token for the specified user email.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password reset initiated if email exists")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userAuthenticationUseCase.requestPasswordReset(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Reset password using token",
            description = "Resets user password with a valid, unexpired reset token.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password successfully reset"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userAuthenticationUseCase.resetPasswordWithToken(
                request.getToken(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Change password for authenticated user",
            description =
                    "Updates the caller's password after verifying their current password. All active sessions are revoked.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password changed successfully"),
        @ApiResponse(
                responseCode = "400",
                description = "Current password invalid or validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        userAuthenticationUseCase.changePassword(
                currentUserId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Logout user",
            description = "Revokes the active refresh token session for the caller.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Logged out successfully")})
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutPost(
            @Parameter(description = "Bearer access token", required = true)
                    @RequestHeader(value = AUTHORIZATION)
                    String bearerToken) {
        return logout(bearerToken);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Bearer access token", required = true)
                    @RequestHeader(value = AUTHORIZATION)
                    String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        userAuthenticationUseCase.logout(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Refresh access token",
            description =
                    "Exchanges a valid refresh token for a fresh pair of access and refresh tokens. Accepts refresh token via Authorization header or JSON request body.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tokens refreshed"),
        @ApiResponse(responseCode = "401", description = "Refresh token expired or revoked")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthLoginResponse> refreshTokenPost(
            @Parameter(description = "Bearer refresh token")
                    @RequestHeader(value = AUTHORIZATION, required = false)
                    String bearerToken,
            @RequestBody(required = false) RefreshTokenRequest request) {
        String token = null;
        if (bearerToken != null && !bearerToken.isBlank()) {
            token = bearerToken.replace("Bearer ", "");
        } else if (request != null
                && request.getRefreshToken() != null
                && !request.getRefreshToken().isBlank()) {
            token = request.getRefreshToken();
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Refresh token is required via Authorization header or request body");
        }
        var result = userAuthenticationUseCase.refreshToken(token);
        return ResponseEntity.ok(AuthRestMapper.toResponse(result));
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<AuthLoginResponse> refreshToken(
            @Parameter(description = "Bearer refresh token", required = true)
                    @RequestHeader(value = AUTHORIZATION)
                    String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        var result = userAuthenticationUseCase.refreshToken(token);
        return ResponseEntity.ok(AuthRestMapper.toResponse(result));
    }

    @Operation(
            summary = "Resolve user principal by token",
            description = "Returns the principal object including all aggregated authorities.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/principal")
    public ResponseEntity<Principal> getPrincipal(
            @Parameter(description = "Bearer access token", required = true)
                    @RequestHeader(value = AUTHORIZATION)
                    String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        Principal principal = userAuthenticationUseCase.getPrincipal(token);
        return ResponseEntity.ok(principal);
    }

    @Operation(
            summary = "Resolve user principal by ID",
            description = "Returns the principal object including all aggregated authorities.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/principal/user")
    public ResponseEntity<Principal> getUserPrincipal(
            @Parameter(description = "Target user ID", required = true) @RequestParam UUID userId) {
        Principal principal = userAuthenticationUseCase.getPrincipal(userId);
        return ResponseEntity.ok(principal);
    }

    @Operation(
            summary = "Resolve partner principal via B2B API keys",
            description =
                    "Validates Client-Id and Client-Key headers and returns the partner principal context.",
            security = {
                @SecurityRequirement(name = "ClientIdHeader"),
                @SecurityRequirement(name = "ClientKeyHeader")
            })
    @GetMapping("/principal/partner")
    public ResponseEntity<Principal> getPartnerPrincipal(
            @Parameter(description = "Partner Client ID", required = true)
                    @RequestHeader("Client-Id")
                    String clientId,
            @Parameter(description = "Partner Client Secret Key", required = true)
                    @RequestHeader("Client-Key")
                    String clientKey) {
        Principal principal = partnerAuthenticationUseCase.getPrincipal(clientId, clientKey);
        return ResponseEntity.ok(principal);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
