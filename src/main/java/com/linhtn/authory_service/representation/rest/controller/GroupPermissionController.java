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

import com.linhtn.authory_service.modules.rbac.service.GroupPermissionService;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupPermissionController {

    private final GroupPermissionService groupPermissionService;

    public GroupPermissionController(GroupPermissionService groupPermissionService) {
        this.groupPermissionService = groupPermissionService;
    }

    /** Get all permission codes of a group */
    @GetMapping("/{groupId}/permissions")
    public ResponseEntity<Set<UUID>> getPermissions(@PathVariable UUID groupId) {
        Set<UUID> permissionIds = groupPermissionService.getPermissionIdsByGroupId(groupId);
        return ResponseEntity.ok(permissionIds);
    }

    /** Replace permissions of a group */
    @PutMapping("/{groupId}/permissions")
    public ResponseEntity<Void> replacePermissions(
            @PathVariable UUID groupId, @RequestBody List<UUID> permissionIds) {
        groupPermissionService.replace(groupId, permissionIds);
        return ResponseEntity.noContent().build();
    }

    /** Add a permission to the group */
    @PostMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<Void> addPermission(
            @PathVariable UUID groupId, @PathVariable UUID permissionId) {
        groupPermissionService.add(groupId, permissionId);
        return ResponseEntity.noContent().build();
    }

    /** Remove a permission from the group */
    @DeleteMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(
            @PathVariable UUID groupId, @PathVariable UUID permissionId) {
        groupPermissionService.remove(groupId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
