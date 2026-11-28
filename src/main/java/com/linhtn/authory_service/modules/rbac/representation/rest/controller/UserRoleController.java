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

import com.linhtn.authory_service.modules.rbac.application.port.in.UserRoleUseCase;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.RoleGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.mapper.RoleRestMapper;
import com.linhtn.security_core.annotation.RequirePermission;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RBAC - User Roles", description = "Assigning roles to users")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequirePermission("RBAC:MANAGE")
@RequestMapping("/api/v1/users")
public class UserRoleController {

    private final UserRoleUseCase userRoleUseCase;

    public UserRoleController(UserRoleUseCase userRoleUseCase) {
        this.userRoleUseCase = userRoleUseCase;
    }

    @Operation(
            summary = "Get user roles",
            description = "Retrieves all roles currently assigned to a user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of assigned roles returned")
    })
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleGetResponse>> getRoles(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(
                userRoleUseCase.getRoles(userId).stream()
                        .map(RoleRestMapper::toGetResponse)
                        .toList());
    }

    @Operation(
            summary = "Replace user roles",
            description =
                    "Replaces the entire set of roles assigned to a user and invalidates cached authorities.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Roles replaced successfully")})
    @PutMapping("/{userId}/roles")
    public ResponseEntity<Void> replaceRoles(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId,
            @RequestBody List<UUID> roleIds) {
        userRoleUseCase.replace(userId, roleIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add role to user",
            description = "Grants a role to a user and invalidates cached authorities.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Role assigned")})
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> addRole(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId,
            @Parameter(description = "Role UUID", required = true) @PathVariable UUID roleId) {
        userRoleUseCase.add(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove role from user",
            description = "Revokes a role from a user and invalidates cached authorities.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Role revoked")})
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRole(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId,
            @Parameter(description = "Role UUID", required = true) @PathVariable UUID roleId) {
        userRoleUseCase.remove(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}
