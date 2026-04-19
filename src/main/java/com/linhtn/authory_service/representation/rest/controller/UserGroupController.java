package com.linhtn.authory_service.representation.rest.controller;

import com.linhtn.authory_service.modules.rbac.domain.dto.GroupDto;
import com.linhtn.authory_service.modules.rbac.service.UserGroupService;
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
public class UserGroupController {

    private final UserGroupService userGroupService;

    public UserGroupController(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    /**
     * Get all groups of a user
     */
    @GetMapping("/{userId}/groups")
    public ResponseEntity<List<GroupDto>> getGroups(@PathVariable UUID userId) {
        return ResponseEntity.ok(userGroupService.getGroups(userId));
    }

    /**
     * Replace groups of a user
     */
    @PutMapping("/{userId}/groups")
    public ResponseEntity<Void> replaceGroups(
            @PathVariable UUID userId,
            @RequestBody List<UUID> groupIds
    ) {
        userGroupService.replace(userId, groupIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a group to user
     */
    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> addGroup(
            @PathVariable UUID userId,
            @PathVariable UUID groupId
    ) {
        userGroupService.add(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove a group from user
     */
    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> removeGroup(
            @PathVariable UUID userId,
            @PathVariable UUID groupId
    ) {
        userGroupService.remove(userId, groupId);
        return ResponseEntity.noContent().build();
    }
}

