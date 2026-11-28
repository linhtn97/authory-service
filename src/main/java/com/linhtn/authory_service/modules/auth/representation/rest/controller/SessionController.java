package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.config.security.util.CurrentPrincipalUtil;
import com.linhtn.authory_service.modules.auth.application.dto.result.TokenResult;
import com.linhtn.authory_service.modules.auth.application.port.in.TokenUseCase;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.SessionGetResponse;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Active Device Sessions",
        description = "Device tracking, active refresh tokens, and remote session termination")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
@RequestMapping("/api/v1/auth/sessions")
public class SessionController {

    private final TokenUseCase tokenUseCase;

    public SessionController(TokenUseCase tokenUseCase) {
        this.tokenUseCase = tokenUseCase;
    }

    @Operation(
            summary = "List all active device sessions",
            description =
                    "Retrieves all unrevoked device sessions and refresh tokens for the currently authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of active sessions returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<SessionGetResponse>> getActiveSessions() {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        List<TokenResult> tokens = tokenUseCase.getActiveSessions(currentUserId);

        List<SessionGetResponse> sessions =
                tokens.stream()
                        .map(
                                t ->
                                        SessionGetResponse.builder()
                                                .id(t.getId())
                                                .ipAddress(t.getIpAddress())
                                                .userAgent(t.getUserAgent())
                                                .deviceName(t.getDeviceName())
                                                .createdAt(t.getCreatedAt())
                                                .expiredAt(t.getExpiredAt())
                                                .current(false)
                                                .build())
                        .toList();

        return ResponseEntity.ok(sessions);
    }

    @Operation(
            summary = "Revoke specific device session",
            description = "Remotely terminates and invalidates a specific device session.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Session successfully revoked"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeSession(
            @Parameter(description = "Session/Token ID to revoke", required = true) @PathVariable
                    UUID id) {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        tokenUseCase.revokeSession(currentUserId, id);
    }

    @Operation(
            summary = "Revoke all other active device sessions",
            description = "Terminates all active sessions for the user across all other devices.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "All other sessions revoked"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/revoke-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeAllOtherSessions() {
        UUID currentUserId = CurrentPrincipalUtil.getId();
        tokenUseCase.revokeAllSessionsExcept(currentUserId, null);
    }
}
