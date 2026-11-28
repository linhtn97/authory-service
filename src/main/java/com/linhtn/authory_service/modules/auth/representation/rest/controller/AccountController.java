package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.config.security.util.CurrentPrincipalUtil;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.ChangePasswordRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.ForgotPasswordRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.RegisterRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.ResetPasswordRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.mapper.AuthRestMapper;
import com.linhtn.idempotency_core.annotation.Idempotent;
import com.linhtn.security_core.annotation.PublicEndpoint;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Account & Password Management",
        description = "User registration, password reset flows, and password updates")
@RestController
@RequestMapping("/api/v1/auth")
public class AccountController {

    private final UserAuthenticationUseCase userAuthenticationUseCase;

    public AccountController(UserAuthenticationUseCase userAuthenticationUseCase) {
        this.userAuthenticationUseCase = userAuthenticationUseCase;
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
    @PublicEndpoint
    @Idempotent
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        userAuthenticationUseCase.register(AuthRestMapper.toCommand(request));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Request password reset email",
            description =
                    "Initiates password reset. Generates a 15-minute token and dispatches reset instructions. "
                            + "Always returns 204 to prevent user enumeration attacks.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password reset instructions dispatched"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded (60 req/min)")
    })
    @PostMapping("/forgot-password")
    @PublicEndpoint
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userAuthenticationUseCase.requestPasswordReset(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Reset password with reset token",
            description =
                    "Validates the one-time reset token and updates the user password. Revokes all active sessions upon success.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    @PostMapping("/reset-password")
    @PublicEndpoint
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userAuthenticationUseCase.resetPasswordWithToken(
                request.getToken(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Change password for authenticated user",
            description =
                    "Verifies current password and sets a new password. Revokes all active user sessions.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password changed successfully"),
        @ApiResponse(
                responseCode = "400",
                description = "Current password invalid or validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/change-password")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        userAuthenticationUseCase.changePassword(
                currentUserId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
