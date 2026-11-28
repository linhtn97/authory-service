package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.config.security.util.CurrentPrincipalUtil;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.MfaDisableRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.MfaEnableRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.MfaVerifyRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.AuthLoginResponse;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.MfaSetupResponse;
import com.linhtn.authory_service.modules.auth.representation.rest.mapper.AuthRestMapper;
import com.linhtn.security_core.annotation.PublicEndpoint;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Multi-Factor Authentication (MFA/2FA)",
        description =
                "RFC 6238 TOTP setup, QR code generation, enablement, disablement, and challenge verification")
@RestController
@RequestMapping("/api/v1/auth/mfa")
public class MfaController {

    private final UserAuthenticationUseCase userAuthenticationUseCase;

    public MfaController(UserAuthenticationUseCase userAuthenticationUseCase) {
        this.userAuthenticationUseCase = userAuthenticationUseCase;
    }

    @Operation(
            summary = "Verify MFA TOTP code during login challenge",
            description =
                    "Validates the 6-digit TOTP code against the temporary mfaToken issued during primary login.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "MFA verified, tokens issued"),
        @ApiResponse(responseCode = "400", description = "Invalid TOTP code or expired MFA token"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded (60 req/min)")
    })
    @PostMapping("/verify")
    @PublicEndpoint
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
            summary = "Setup MFA / 2FA secret and QR code URI",
            description =
                    "Generates a Base32 TOTP secret and otpauth:// URI for Google Authenticator.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "MFA secret and QR code URI generated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/setup")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<MfaSetupResponse> setupMfa() {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        var result = userAuthenticationUseCase.setupMfa(currentUserId);
        return ResponseEntity.ok(AuthRestMapper.toMfaSetupResponse(result));
    }

    @Operation(
            summary = "Enable MFA after confirming TOTP code",
            description =
                    "Validates the first TOTP code from user authenticator app and activates MFA enforcement.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "MFA enabled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid verification code"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/enable")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<Void> enableMfa(@Valid @RequestBody MfaEnableRequest request) {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        userAuthenticationUseCase.enableMfa(currentUserId, request.getCode());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Disable MFA with password confirmation",
            description = "Disables MFA enforcement for caller upon validating account password.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "MFA disabled successfully"),
        @ApiResponse(responseCode = "400", description = "Password verification failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/disable")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<Void> disableMfa(@Valid @RequestBody MfaDisableRequest request) {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        userAuthenticationUseCase.disableMfa(currentUserId, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
