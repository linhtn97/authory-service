package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.auth.application.dto.command.ApiKeyCreateCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.application.port.in.ApiCredentialUseCase;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.ApiKeyCreateRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.ApiKeyCreateResponse;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.ApiKeyResponse;
import com.linhtn.authory_service.modules.auth.representation.rest.mapper.ApiKeyRestMapper;
import com.linhtn.security_core.annotation.CurrentUser;
import com.linhtn.security_core.annotation.RequireRole;
import com.linhtn.security_core.model.AuthenticatedUser;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "API Key Management",
        description = "Issuance, query, rotation, and revocation of automated API credentials")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/api-keys")
public class ApiKeyController {

    private final ApiCredentialUseCase apiCredentialUseCase;

    public ApiKeyController(ApiCredentialUseCase apiCredentialUseCase) {
        this.apiCredentialUseCase = apiCredentialUseCase;
    }

    @Operation(
            summary = "Issue new API key",
            description =
                    "Creates an API key and Client Credentials for the authenticated caller. Returns full secrets once.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "API key created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<ApiKeyCreateResponse> issueApiKey(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody(required = false) ApiKeyCreateRequest request) {
        UUID userId = UUID.fromString(currentUser.getUserId());
        String name = request != null ? request.getName() : "Default API Key";
        var expiredAt = request != null ? request.getExpiredAt() : null;

        ApiKeyCreateCommand command =
                ApiKeyCreateCommand.builder()
                        .userId(userId)
                        .name(name)
                        .expiredAt(expiredAt)
                        .build();

        ApiCredentialResult result = apiCredentialUseCase.issue(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiKeyRestMapper.toCreateResponse(result));
    }

    @Operation(
            summary = "List all API keys for current caller",
            description =
                    "Retrieves all API keys belonging to the authenticated caller with masked secrets.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of API keys returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/my")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<List<ApiKeyResponse>> getMyApiKeys(
            @CurrentUser AuthenticatedUser currentUser) {
        UUID userId = UUID.fromString(currentUser.getUserId());
        List<ApiCredentialResult> results = apiCredentialUseCase.getMyApiKeys(userId);
        return ResponseEntity.ok(ApiKeyRestMapper.toResponseList(results));
    }

    @Operation(
            summary = "Get API key details by ID",
            description = "Retrieves metadata of a specific API key (masked) by ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "API key details returned"),
        @ApiResponse(responseCode = "404", description = "API key not found")
    })
    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<ApiKeyResponse> getApiKey(
            @Parameter(description = "API key ID", required = true) @PathVariable UUID id,
            @CurrentUser AuthenticatedUser currentUser) {
        UUID userId = UUID.fromString(currentUser.getUserId());
        boolean isAdmin = isAdmin(currentUser);
        ApiCredentialResult result = apiCredentialUseCase.getByIdAndUser(id, userId, isAdmin);
        return ResponseEntity.ok(ApiKeyRestMapper.toResponse(result));
    }

    @Operation(
            summary = "Revoke API key",
            description = "Revokes an API key immediately and invalidates its cached credentials.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "API key revoked successfully"),
        @ApiResponse(responseCode = "404", description = "API key not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<Void> revokeApiKey(
            @Parameter(description = "API key ID", required = true) @PathVariable UUID id,
            @CurrentUser AuthenticatedUser currentUser) {
        UUID userId = UUID.fromString(currentUser.getUserId());
        boolean isAdmin = isAdmin(currentUser);
        apiCredentialUseCase.revoke(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Rotate API key credentials",
            description =
                    "Generates a new API key and secret while preserving metadata. Returns full secrets once.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "API key rotated successfully"),
        @ApiResponse(responseCode = "404", description = "API key not found")
    })
    @PostMapping("/{id}/rotate")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<ApiKeyCreateResponse> rotateApiKey(
            @Parameter(description = "API key ID", required = true) @PathVariable UUID id,
            @CurrentUser AuthenticatedUser currentUser) {
        UUID userId = UUID.fromString(currentUser.getUserId());
        boolean isAdmin = isAdmin(currentUser);
        ApiCredentialResult result = apiCredentialUseCase.rotate(id, userId, isAdmin);
        return ResponseEntity.ok(ApiKeyRestMapper.toCreateResponse(result));
    }

    @Operation(
            summary = "Query paginated API keys (Admin)",
            description = "Administratively query paginated API keys across all users.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated API keys returned"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/get-page")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<PagingResponse<ApiKeyResponse>> getPage(
            @Valid @RequestBody PagingRequest request) {
        var paged = apiCredentialUseCase.getPage(request);
        return ResponseEntity.ok(paged.map(ApiKeyRestMapper::toResponse));
    }

    private boolean isAdmin(AuthenticatedUser user) {
        return user != null
                && user.getRoles() != null
                && (user.getRoles().contains("ADMIN")
                        || user.getRoles().contains("SUPER_ADMIN")
                        || user.getRoles().contains("ROLE_ADMIN")
                        || user.getRoles().contains("ROLE_SUPER_ADMIN"));
    }
}
