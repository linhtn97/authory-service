package com.linhtn.authory_service.representation.rest.controller;

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

import com.linhtn.authory_service.modules.rbac.service.RolePermissionService;

@RestController
@RequestMapping("/api/v1/roles")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    /** Get all permission codes of a role */
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<Set<UUID>> getPermissions(@PathVariable UUID roleId) {
        Set<UUID> permissionIds = rolePermissionService.getPermissionIdsByRoleId(roleId);
        return ResponseEntity.ok(permissionIds);
    }

    /** Replace permissions of a role */
    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<Void> replacePermissions(
            @PathVariable UUID roleId, @RequestBody List<UUID> permissionIds) {
        rolePermissionService.replace(roleId, permissionIds);
        return ResponseEntity.noContent().build();
    }

    /** Add a permission to the role */
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> addPermission(
            @PathVariable UUID roleId, @PathVariable UUID permissionId) {
        rolePermissionService.add(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }

    /** Remove a permission from the role */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(
            @PathVariable UUID roleId, @PathVariable UUID permissionId) {
        rolePermissionService.remove(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
