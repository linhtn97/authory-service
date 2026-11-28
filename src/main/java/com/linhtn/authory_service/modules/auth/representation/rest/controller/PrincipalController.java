package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.security_core.annotation.PublicEndpoint;
import com.linhtn.security_core.annotation.RequireRole;
import com.linhtn.security_core.model.AuthenticatedUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Principal Resolution",
        description =
                "Resolve current user principal via JWT token, API Key, or Client Credentials")
@RestController
@RequestMapping("/api/v1/auth/principal")
public class PrincipalController {

    private final UserAuthenticationUseCase userAuthenticationUseCase;

    public PrincipalController(UserAuthenticationUseCase userAuthenticationUseCase) {
        this.userAuthenticationUseCase = userAuthenticationUseCase;
    }

    @Operation(
            summary = "Resolve user principal by token",
            description = "Returns the principal object including all aggregated authorities.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<AuthenticatedUser> getPrincipal(
            @Parameter(description = "Bearer access token", required = true)
                    @RequestHeader(value = AUTHORIZATION)
                    String bearerToken) {
        String token = bearerToken.replaceFirst("(?i)bearer\\s+", "").trim();
        AuthenticatedUser principal = userAuthenticationUseCase.getPrincipal(token);
        return ResponseEntity.ok(principal);
    }

    @Operation(
            summary = "Resolve user principal by ID",
            description = "Returns the principal object including all aggregated authorities.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/user")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<AuthenticatedUser> getUserPrincipal(
            @Parameter(description = "Target user ID", required = true) @RequestParam UUID userId) {
        AuthenticatedUser principal = userAuthenticationUseCase.getPrincipal(userId);
        return ResponseEntity.ok(principal);
    }

    @Operation(
            summary = "Resolve user principal via API Key",
            description = "Validates X-API-KEY header and returns the user principal context.",
            security = @SecurityRequirement(name = "ApiKeyAuth"))
    @GetMapping("/api-key")
    @PublicEndpoint
    public ResponseEntity<AuthenticatedUser> getPrincipalByApiKey(
            @Parameter(description = "API Key", required = true) @RequestHeader("X-API-KEY")
                    String apiKey) {
        AuthenticatedUser principal = userAuthenticationUseCase.getPrincipalByApiKey(apiKey);
        return ResponseEntity.ok(principal);
    }

    @Operation(
            summary = "Resolve user principal via Client Credentials",
            description =
                    "Validates Client-Id and Client-Key headers and returns the user principal context.",
            security = {
                @SecurityRequirement(name = "ClientIdHeader"),
                @SecurityRequirement(name = "ClientKeyHeader")
            })
    @GetMapping("/client")
    @PublicEndpoint
    public ResponseEntity<AuthenticatedUser> getPrincipalByClientCredentials(
            @Parameter(description = "Client ID", required = true) @RequestHeader("Client-Id")
                    String clientId,
            @Parameter(description = "Client Secret Key", required = true)
                    @RequestHeader("Client-Key")
                    String clientKey) {
        AuthenticatedUser principal =
                userAuthenticationUseCase.getPrincipalByClientCredentials(clientId, clientKey);
        return ResponseEntity.ok(principal);
    }
}
