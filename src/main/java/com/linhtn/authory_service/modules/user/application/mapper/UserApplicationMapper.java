package com.linhtn.authory_service.modules.user.application.mapper;

import com.linhtn.authory_service.modules.user.application.dto.command.UserCreateCommand;
import com.linhtn.authory_service.modules.user.application.dto.command.UserUpdateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserCreateResult;
import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.dto.result.UserUpdateResult;
import com.linhtn.authory_service.modules.user.domain.model.User;

public class UserApplicationMapper {

    private UserApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static User toDomain(UserCreateCommand command) {
        if (command == null) {
            return null;
        }
        return User.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .build();
    }

    public static void updateDomain(User user, UserUpdateCommand command) {
        if (user == null || command == null) {
            return;
        }
        user.setUsername(command.getUsername());
        user.setEmail(command.getEmail());
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
    }

    public static UserCreateResult toCreateResult(User user) {
        if (user == null) return null;
        return UserCreateResult.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .version(user.getVersion())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    public static UserUpdateResult toUpdateResult(User user) {
        if (user == null) return null;
        return UserUpdateResult.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .version(user.getVersion())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    public static UserGetResult toGetResult(User user) {
        if (user == null) return null;
        return UserGetResult.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .version(user.getVersion())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }
}
