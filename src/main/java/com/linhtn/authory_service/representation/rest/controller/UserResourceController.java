package com.linhtn.authory_service.representation.rest.controller;

import com.linhtn.authory_service.modules.rbac.domain.dto.ResourceDto;
import com.linhtn.authory_service.modules.rbac.service.UserResourceService;
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
public class UserResourceController {

    private final UserResourceService userResourceService;

    public UserResourceController(UserResourceService userResourceService) {
        this.userResourceService = userResourceService;
    }

    /**
     * Get all resources of a user
     */
    @GetMapping("/{userId}/resources")
    public ResponseEntity<List<ResourceDto>> getResources(@PathVariable UUID userId) {
        return ResponseEntity.ok(userResourceService.getResources(userId));
    }

    /**
     * Replace user's resources
     */
    @PutMapping("/{userId}/resources")
    public ResponseEntity<Void> replaceResources(
            @PathVariable UUID userId,
            @RequestBody List<UUID> resourceIds
    ) {
        userResourceService.replace(userId, resourceIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a resource to user
     */
    @PostMapping("/{userId}/resources/{resourceId}")
    public ResponseEntity<Void> addResource(
            @PathVariable UUID userId,
            @PathVariable UUID resourceId
    ) {
        userResourceService.add(userId, resourceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove a resource from user
     */
    @DeleteMapping("/{userId}/resources/{resourceId}")
    public ResponseEntity<Void> removeResource(
            @PathVariable UUID userId,
            @PathVariable UUID resourceId
    ) {
        userResourceService.remove(userId, resourceId);
        return ResponseEntity.noContent().build();
    }
}

