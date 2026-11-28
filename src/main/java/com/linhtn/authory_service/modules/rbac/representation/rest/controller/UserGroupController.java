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

import com.linhtn.authory_service.modules.rbac.application.port.in.UserGroupUseCase;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.GroupGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.mapper.GroupRestMapper;
import com.linhtn.security_core.annotation.RequirePermission;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RBAC - User Groups", description = "Assigning users to groups")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequirePermission("RBAC:MANAGE")
@RequestMapping("/api/v1/users")
public class UserGroupController {

    private final UserGroupUseCase userGroupUseCase;

    public UserGroupController(UserGroupUseCase userGroupUseCase) {
        this.userGroupUseCase = userGroupUseCase;
    }

    @Operation(
            summary = "Get user groups",
            description = "Retrieves all groups the user belongs to.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "List of groups returned")})
    @GetMapping("/{userId}/groups")
    public ResponseEntity<List<GroupGetResponse>> getGroups(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(
                userGroupUseCase.getGroups(userId).stream()
                        .map(GroupRestMapper::toGetResponse)
                        .toList());
    }

    @Operation(
            summary = "Replace user groups",
            description =
                    "Replaces the entire set of groups for a user and invalidates cached authorities.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Groups replaced successfully")
    })
    @PutMapping("/{userId}/groups")
    public ResponseEntity<Void> replaceGroups(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId,
            @RequestBody List<UUID> groupIds) {
        userGroupUseCase.replace(userId, groupIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add group to user",
            description = "Assigns user to a group and invalidates cached authorities.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "User added to group")})
    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> addGroup(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId,
            @Parameter(description = "Group UUID", required = true) @PathVariable UUID groupId) {
        userGroupUseCase.add(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove group from user",
            description = "Removes user from a group and invalidates cached authorities.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "User removed from group")})
    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> removeGroup(
            @Parameter(description = "User UUID", required = true) @PathVariable UUID userId,
            @Parameter(description = "Group UUID", required = true) @PathVariable UUID groupId) {
        userGroupUseCase.remove(userId, groupId);
        return ResponseEntity.noContent().build();
    }
}
