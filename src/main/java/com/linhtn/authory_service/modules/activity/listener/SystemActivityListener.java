package com.linhtn.authory_service.modules.activity.listener;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.linhtn.authory_service.modules.activity.domain.model.SystemActivity;
import com.linhtn.authory_service.modules.activity.domain.repository.SystemActivityRepository;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupCreatedEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupDeletedEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupUpdatedEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionCreatedEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionDeletedEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionUpdatedEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleCreatedEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleDeletedEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleUpdatedEvent;
import com.linhtn.authory_service.modules.user.domain.event.UserCreatedEvent;
import com.linhtn.authory_service.modules.user.domain.event.UserDeletedEvent;
import com.linhtn.authory_service.modules.user.domain.event.UserUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemActivityListener {

    private final SystemActivityRepository systemActivityRepository;

    // --- User events ---
    @Async
    @EventListener
    public void handleUserCreate(UserCreatedEvent event) {
        var user = event.getUser();
        saveActivity(
                "User <b>" + user.getUsername() + "</b> was created",
                "USER",
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    @Async
    @EventListener
    public void handleUserUpdate(UserUpdatedEvent event) {
        var user = event.getUser();
        saveActivity(
                "User <b>" + user.getUsername() + "</b> was updated",
                "USER",
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    @Async
    @EventListener
    public void handleUserDelete(UserDeletedEvent event) {
        var user = event.getUser();
        saveActivity(
                "User <b>" + user.getUsername() + "</b> was deleted",
                "USER",
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    // --- Role events ---
    @Async
    @EventListener
    public void handleRoleCreate(RoleCreatedEvent event) {
        var role = event.getRole();
        saveActivity(
                "Role <b>" + role.getName() + "</b> was created",
                "ROLE",
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    @Async
    @EventListener
    public void handleRoleUpdate(RoleUpdatedEvent event) {
        var role = event.getRole();
        saveActivity(
                "Role <b>" + role.getName() + "</b> was updated",
                "ROLE",
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    @Async
    @EventListener
    public void handleRoleDelete(RoleDeletedEvent event) {
        var role = event.getRole();
        saveActivity(
                "Role <b>" + role.getName() + "</b> was deleted",
                "ROLE",
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    // --- Group events ---
    @Async
    @EventListener
    public void handleGroupCreate(GroupCreatedEvent event) {
        var group = event.getGroup();
        saveActivity(
                "Group <b>" + group.getName() + "</b> was created",
                "GROUP",
                group.getCreatedAt(),
                group.getUpdatedAt());
    }

    @Async
    @EventListener
    public void handleGroupUpdate(GroupUpdatedEvent event) {
        var group = event.getGroup();
        saveActivity(
                "Group <b>" + group.getName() + "</b> was updated",
                "GROUP",
                group.getCreatedAt(),
                group.getUpdatedAt());
    }

    @Async
    @EventListener
    public void handleGroupDelete(GroupDeletedEvent event) {
        var group = event.getGroup();
        saveActivity(
                "Group <b>" + group.getName() + "</b> was deleted",
                "GROUP",
                group.getCreatedAt(),
                group.getUpdatedAt());
    }

    // --- Permission events ---
    @Async
    @EventListener
    public void handlePermissionCreate(PermissionCreatedEvent event) {
        var permission = event.getPermission();
        saveActivity(
                "Permission <b>" + permission.getName() + "</b> was created",
                "PERMISSION",
                permission.getCreatedAt(),
                permission.getUpdatedAt());
    }

    @Async
    @EventListener
    public void handlePermissionUpdate(PermissionUpdatedEvent event) {
        var permission = event.getPermission();
        saveActivity(
                "Permission <b>" + permission.getName() + "</b> was updated",
                "PERMISSION",
                permission.getCreatedAt(),
                permission.getUpdatedAt());
    }

    @Async
    @EventListener
    public void handlePermissionDelete(PermissionDeletedEvent event) {
        var permission = event.getPermission();
        saveActivity(
                "Permission <b>" + permission.getName() + "</b> was deleted",
                "PERMISSION",
                permission.getCreatedAt(),
                permission.getUpdatedAt());
    }

    private void saveActivity(
            String text, String iconType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        try {
            SystemActivity activity =
                    SystemActivity.builder()
                            .text(text)
                            .iconType(iconType)
                            .createdAt(createdAt)
                            .updatedAt(updatedAt)
                            .build();
            systemActivityRepository.save(activity);
        } catch (Exception e) {
            log.error("Failed to save system activity: {}", e.getMessage(), e);
        }
    }
}
