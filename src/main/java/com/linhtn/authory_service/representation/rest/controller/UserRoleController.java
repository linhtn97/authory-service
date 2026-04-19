package com.linhtn.authory_service.representation.rest.controller;

import com.linhtn.authory_service.modules.rbac.domain.dto.RoleDto;
import com.linhtn.authory_service.modules.rbac.service.UserRoleService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * Get all roles of a user
     */
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleDto>> getRoles(@PathVariable UUID userId) {
        return ResponseEntity.ok(userRoleService.getRoles(userId));
    }

    /**
     * Replace roles of a user
     */
    @PutMapping("/{userId}/roles")
    public ResponseEntity<Void> replaceRoles(
            @PathVariable UUID userId,
            @RequestBody List<UUID> roleIds
    ) {
        userRoleService.replace(userId, roleIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a role to user
     */
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> addRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId
    ) {
        userRoleService.add(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove a role from user
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId
    ) {
        userRoleService.remove(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}

