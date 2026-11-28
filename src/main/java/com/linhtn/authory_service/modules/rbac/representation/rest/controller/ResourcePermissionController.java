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

import com.linhtn.authory_service.modules.rbac.application.port.in.ResourcePermissionUseCase;
import com.linhtn.security_core.annotation.RequirePermission;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RBAC - Resource Permissions", description = "Binding permissions to resources")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequirePermission("RBAC:MANAGE")
@RequestMapping("/api/v1/resources")
public class ResourcePermissionController {

    private final ResourcePermissionUseCase resourcePermissionUseCase;

    public ResourcePermissionController(ResourcePermissionUseCase resourcePermissionUseCase) {
        this.resourcePermissionUseCase = resourcePermissionUseCase;
    }

    @Operation(
            summary = "Get permissions assigned to resource",
            description = "Retrieves all permission IDs bound to the specified resource.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Set of permission IDs returned")
    })
    @GetMapping("/{resourceId}/permissions")
    public ResponseEntity<Set<UUID>> getPermissions(
            @Parameter(description = "Resource UUID", required = true) @PathVariable
                    UUID resourceId) {
        Set<UUID> permissionIds =
                resourcePermissionUseCase.getPermissionIdsByResourceId(resourceId);
        return ResponseEntity.ok(permissionIds);
    }

    @Operation(
            summary = "Replace permissions of resource",
            description = "Replaces the entire set of permissions bound to a resource.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Permissions replaced successfully")
    })
    @PutMapping("/{resourceId}/permissions")
    public ResponseEntity<Void> replacePermissions(
            @Parameter(description = "Resource UUID", required = true) @PathVariable
                    UUID resourceId,
            @RequestBody List<UUID> permissionIds) {
        resourcePermissionUseCase.replace(resourceId, permissionIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add permission to resource",
            description = "Grants a permission to a resource.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Permission added")})
    @PostMapping("/{resourceId}/permissions/{permissionId}")
    public ResponseEntity<Void> addPermission(
            @Parameter(description = "Resource UUID", required = true) @PathVariable
                    UUID resourceId,
            @Parameter(description = "Permission UUID", required = true) @PathVariable
                    UUID permissionId) {
        resourcePermissionUseCase.add(resourceId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove permission from resource",
            description = "Revokes a permission from a resource.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Permission removed")})
    @DeleteMapping("/{resourceId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(
            @Parameter(description = "Resource UUID", required = true) @PathVariable
                    UUID resourceId,
            @Parameter(description = "Permission UUID", required = true) @PathVariable
                    UUID permissionId) {
        resourcePermissionUseCase.remove(resourceId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
