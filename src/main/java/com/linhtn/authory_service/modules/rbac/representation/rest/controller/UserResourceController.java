package com.linhtn.authory_service.modules.rbac.representation.rest.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.rbac.application.port.in.UserResourceUseCase;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.ResourceGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.mapper.ResourceRestMapper;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RBAC - User Resources", description = "Granting direct resource access to users")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/users")
public class UserResourceController {

    private final UserResourceUseCase userResourceUseCase;

    public UserResourceController(UserResourceUseCase userResourceUseCase) {
        this.userResourceUseCase = userResourceUseCase;
    }

    @Operation(
            summary = "Get user resources",
            description = "Retrieves all resources directly assigned to a user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of assigned resources returned")
    })
    @GetMapping("/{userId}/resources")
    public ResponseEntity<List<ResourceGetResponse>> getResources(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(
                userResourceUseCase.getResources(userId).stream()
                        .map(ResourceRestMapper::toGetResponse)
                        .toList());
    }

    @Operation(
            summary = "Replace user resources",
            description =
                    "Replaces the entire set of direct resources assigned to a user and invalidates cached authorities.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Resources replaced successfully")
    })
    @PutMapping("/{userId}/resources")
    public ResponseEntity<Void> replaceResources(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId,
            @RequestBody List<UUID> resourceIds) {
        userResourceUseCase.replace(userId, resourceIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add resource to user",
            description =
                    "Grants direct resource access to a user and invalidates cached authorities.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Resource assigned")})
    @PostMapping("/{userId}/resources/{resourceId}")
    public ResponseEntity<Void> addResource(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId,
            @Parameter(description = "Resource UUID", required = true) @PathVariable
                    UUID resourceId) {
        userResourceUseCase.add(userId, resourceId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove resource from user",
            description =
                    "Revokes direct resource access from a user and invalidates cached authorities.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Resource revoked")})
    @DeleteMapping("/{userId}/resources/{resourceId}")
    public ResponseEntity<Void> removeResource(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId,
            @Parameter(description = "Resource UUID", required = true) @PathVariable
                    UUID resourceId) {
        userResourceUseCase.remove(userId, resourceId);
        return ResponseEntity.noContent().build();
    }
}
