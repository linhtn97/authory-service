package com.linhtn.authory_service.modules.abac.application.mapper;

import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeUpdateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeCreateResult;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeGetResult;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeUpdateResult;
import com.linhtn.authory_service.modules.abac.domain.model.UserAttribute;

public class UserAttributeApplicationMapper {
    private UserAttributeApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static UserAttribute toDomain(UserAttributeCreateCommand command) {
        if (command == null) return null;
        return UserAttribute.builder()
                .key(command.getKey())
                .value(command.getValue())
                .userId(command.getUserId())
                .build();
    }

    public static void updateDomain(UserAttribute model, UserAttributeUpdateCommand command) {
        if (model == null || command == null) return;
        if (command.getKey() != null) model.setKey(command.getKey());
        if (command.getValue() != null) model.setValue(command.getValue());
        if (command.getUserId() != null) model.setUserId(command.getUserId());
    }

    public static UserAttributeCreateResult toCreateResult(UserAttribute model) {
        if (model == null) return null;
        return UserAttributeCreateResult.builder()
                .id(model.getId())
                .key(model.getKey())
                .value(model.getValue())
                .userId(model.getUserId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static UserAttributeUpdateResult toUpdateResult(UserAttribute model) {
        if (model == null) return null;
        return UserAttributeUpdateResult.builder()
                .id(model.getId())
                .key(model.getKey())
                .value(model.getValue())
                .userId(model.getUserId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static UserAttributeGetResult toGetResult(UserAttribute model) {
        if (model == null) return null;
        return UserAttributeGetResult.builder()
                .id(model.getId())
                .key(model.getKey())
                .value(model.getValue())
                .userId(model.getUserId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }
}
