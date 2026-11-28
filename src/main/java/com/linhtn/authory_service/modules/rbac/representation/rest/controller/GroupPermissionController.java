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

import com.linhtn.authory_service.modules.rbac.application.port.in.GroupPermissionUseCase;
import com.linhtn.security_core.annotation.RequirePermission;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RBAC - Group Permissions", description = "Binding permissions to groups")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequirePermission("RBAC:MANAGE")
@RequestMapping("/api/v1/groups")
public class GroupPermissionController {

    private final GroupPermissionUseCase groupPermissionUseCase;

    public GroupPermissionController(GroupPermissionUseCase groupPermissionUseCase) {
        this.groupPermissionUseCase = groupPermissionUseCase;
    }

    @Operation(
            summary = "Get permissions assigned to group",
            description = "Retrieves all permission IDs bound to the specified group.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Set of permission IDs returned")
    })
    @GetMapping("/{groupId}/permissions")
    public ResponseEntity<Set<UUID>> getPermissions(
            @Parameter(description = "Group UUID", required = true) @PathVariable UUID groupId) {
        Set<UUID> permissionIds = groupPermissionUseCase.getPermissionIdsByGroupId(groupId);
        return ResponseEntity.ok(permissionIds);
    }

    @Operation(
            summary = "Replace permissions of group",
            description =
                    "Replaces the entire set of permissions bound to a group and invalidates cached authorities.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Permissions replaced successfully")
    })
    @PutMapping("/{groupId}/permissions")
    public ResponseEntity<Void> replacePermissions(
            @Parameter(description = "Group UUID", required = true) @PathVariable UUID groupId,
            @RequestBody List<UUID> permissionIds) {
        groupPermissionUseCase.replace(groupId, permissionIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add permission to group",
            description = "Grants a permission to a group and invalidates cache.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Permission added")})
    @PostMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<Void> addPermission(
            @Parameter(description = "Group UUID", required = true) @PathVariable UUID groupId,
            @Parameter(description = "Permission UUID", required = true) @PathVariable
                    UUID permissionId) {
        groupPermissionUseCase.add(groupId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove permission from group",
            description = "Revokes a permission from a group and invalidates cache.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Permission removed")})
    @DeleteMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(
            @Parameter(description = "Group UUID", required = true) @PathVariable UUID groupId,
            @Parameter(description = "Permission UUID", required = true) @PathVariable
                    UUID permissionId) {
        groupPermissionUseCase.remove(groupId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
