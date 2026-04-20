package com.linhtn.authory_service.modules.activity.listener;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.linhtn.authory_service.modules.activity.domain.entity.SystemActivity;
import com.linhtn.authory_service.modules.activity.repository.SystemActivityRepository;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupCreateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupDeleteEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupUpdateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionCreateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionDeleteEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionUpdateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleCreateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleDeleteEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleUpdateEvent;
import com.linhtn.authory_service.modules.user.domain.event.UserCreateEvent;
import com.linhtn.authory_service.modules.user.domain.event.UserDeleteEvent;
import com.linhtn.authory_service.modules.user.domain.event.UserUpdateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemActivityListener {

    private final SystemActivityRepository systemActivityRepository;

    // --- User events ---
    @EventListener
    public void handleUserCreate(UserCreateEvent event) {
        var user = event.getUser();
        saveActivity(
                "User <b>" + user.getUsername() + "</b> was created",
                "USER",
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    @EventListener
    public void handleUserUpdate(UserUpdateEvent event) {
        var user = event.getUser();
        saveActivity(
                "User <b>" + user.getUsername() + "</b> was updated",
                "USER",
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    @EventListener
    public void handleUserDelete(UserDeleteEvent event) {
        var user = event.getUser();
        saveActivity(
                "User <b>" + user.getUsername() + "</b> was deleted",
                "USER",
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    // --- Role events ---
    @EventListener
    public void handleRoleCreate(RoleCreateEvent event) {
        var role = event.getRole();
        saveActivity(
                "Role <b>" + role.getName() + "</b> was created",
                "ROLE",
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    @EventListener
    public void handleRoleUpdate(RoleUpdateEvent event) {
        var role = event.getRole();
        saveActivity(
                "Role <b>" + role.getName() + "</b> was updated",
                "ROLE",
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    @EventListener
    public void handleRoleDelete(RoleDeleteEvent event) {
        var role = event.getRole();
        saveActivity(
                "Role <b>" + role.getName() + "</b> was deleted",
                "ROLE",
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    // --- Group events ---
    @EventListener
    public void handleGroupCreate(GroupCreateEvent event) {
        var group = event.getGroup();
        saveActivity(
                "Group <b>" + group.getName() + "</b> was created",
                "GROUP",
                group.getCreatedAt(),
                group.getUpdatedAt());
    }

    @EventListener
    public void handleGroupUpdate(GroupUpdateEvent event) {
        var group = event.getGroup();
        saveActivity(
                "Group <b>" + group.getName() + "</b> was updated",
                "GROUP",
                group.getCreatedAt(),
                group.getUpdatedAt());
    }

    @EventListener
    public void handleGroupDelete(GroupDeleteEvent event) {
        var group = event.getGroup();
        saveActivity(
                "Group <b>" + group.getName() + "</b> was deleted",
                "GROUP",
                group.getCreatedAt(),
                group.getUpdatedAt());
    }

    // --- Permission events ---
    @EventListener
    public void handlePermissionCreate(PermissionCreateEvent event) {
        var permission = event.getPermission();
        saveActivity(
                "Permission <b>" + permission.getName() + "</b> was created",
                "PERMISSION",
                permission.getCreatedAt(),
                permission.getUpdatedAt());
    }

    @EventListener
    public void handlePermissionUpdate(PermissionUpdateEvent event) {
        var permission = event.getPermission();
        saveActivity(
                "Permission <b>" + permission.getName() + "</b> was updated",
                "PERMISSION",
                permission.getCreatedAt(),
                permission.getUpdatedAt());
    }

    @EventListener
    public void handlePermissionDelete(PermissionDeleteEvent event) {
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
            SystemActivity activity = new SystemActivity();
            activity.setText(text);
            activity.setIconType(iconType);
            activity.setCreatedAt(createdAt);
            activity.setUpdatedAt(updatedAt);
            systemActivityRepository.save(activity);
        } catch (Exception e) {
            log.error("Failed to save system activity: {}", e.getMessage(), e);
        }
    }
}
