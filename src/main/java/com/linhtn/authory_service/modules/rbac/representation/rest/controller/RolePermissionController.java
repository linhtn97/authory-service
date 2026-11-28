package com.linhtn.authory_service.modules.rbac.representation.rest.controller;

import java.util.List;
import java.util.Set;
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

import com.linhtn.authory_service.modules.rbac.application.port.in.RolePermissionUseCase;
import com.linhtn.security_core.annotation.RequirePermission;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RBAC - Role Permissions", description = "Binding permissions to roles")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequirePermission("RBAC:MANAGE")
@RequestMapping("/api/v1/roles")
public class RolePermissionController {

    private final RolePermissionUseCase rolePermissionUseCase;

    public RolePermissionController(RolePermissionUseCase rolePermissionUseCase) {
        this.rolePermissionUseCase = rolePermissionUseCase;
    }

    @Operation(
            summary = "Get permissions assigned to role",
            description = "Retrieves all permission IDs bound to the specified role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Set of permission IDs returned")
    })
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<Set<UUID>> getPermissions(
            @Parameter(description = "Role UUID", required = true) @PathVariable UUID roleId) {
        Set<UUID> permissionIds = rolePermissionUseCase.getPermissionIdsByRoleId(roleId);
        return ResponseEntity.ok(permissionIds);
    }

    @Operation(
            summary = "Replace permissions of role",
            description =
                    "Replaces the entire set of permissions bound to a role and invalidates cached user authorities.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Permissions replaced successfully")
    })
    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<Void> replacePermissions(
            @Parameter(description = "Role UUID", required = true) @PathVariable UUID roleId,
            @RequestBody List<UUID> permissionIds) {
        rolePermissionUseCase.replace(roleId, permissionIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add permission to role",
            description = "Grants a specific permission to a role and invalidates cache.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Permission added")})
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> addPermission(
            @Parameter(description = "Role UUID", required = true) @PathVariable UUID roleId,
            @Parameter(description = "Permission UUID", required = true) @PathVariable
                    UUID permissionId) {
        rolePermissionUseCase.add(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove permission from role",
            description = "Revokes a specific permission from a role and invalidates cache.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Permission removed")})
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(
            @Parameter(description = "Role UUID", required = true) @PathVariable UUID roleId,
            @Parameter(description = "Permission UUID", required = true) @PathVariable
                    UUID permissionId) {
        rolePermissionUseCase.remove(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
