package com.linhtn.authory_service.representation.rest.controller;

import com.linhtn.authory_service.modules.rbac.service.ResourcePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourcePermissionController {

    private final ResourcePermissionService resourcePermissionService;

    public ResourcePermissionController(ResourcePermissionService resourcePermissionService) {
        this.resourcePermissionService = resourcePermissionService;
    }

    /**
     * Get all permission codes of a resource
     */
    @GetMapping("/{resourceId}/permissions")
    public ResponseEntity<Set<UUID>> getPermissions(@PathVariable UUID resourceId) {
        Set<UUID> permissionIds = resourcePermissionService.getPermissionIdsByResourceId(resourceId);
        return ResponseEntity.ok(permissionIds);
    }

    /**
     * Replace permissions of a resource
     */
    @PutMapping("/{resourceId}/permissions")
    public ResponseEntity<Void> replacePermissions(
            @PathVariable UUID resourceId,
            @RequestBody List<UUID> permissionIds
    ) {
        resourcePermissionService.replace(resourceId, permissionIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a permission to the resource
     */
    @PostMapping("/{resourceId}/permissions/{permissionId}")
    public ResponseEntity<Void> addPermission(
            @PathVariable UUID resourceId,
            @PathVariable UUID permissionId
    ) {
        resourcePermissionService.add(resourceId, permissionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove a permission from the resource
     */
    @DeleteMapping("/{resourceId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(
            @PathVariable UUID resourceId,
            @PathVariable UUID permissionId
    ) {
        resourcePermissionService.remove(resourceId, permissionId);
        return ResponseEntity.noContent().build();
    }
}

