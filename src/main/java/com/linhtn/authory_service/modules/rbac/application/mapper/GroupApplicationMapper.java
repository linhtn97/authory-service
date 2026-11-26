package com.linhtn.authory_service.modules.rbac.application.mapper;

import com.linhtn.authory_service.modules.rbac.application.dto.command.GroupCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.command.GroupUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupCreateResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupUpdateResult;
import com.linhtn.authory_service.modules.rbac.domain.model.Group;

public class GroupApplicationMapper {

    private GroupApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Group toDomain(GroupCreateCommand command) {
        if (command == null) return null;
        var builder = Group.builder().code(command.getCode()).name(command.getName());
        var model = builder.build();
        return model;
    }

    public static void updateDomain(Group model, GroupUpdateCommand command) {
        if (model == null || command == null) return;
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
    }

    public static GroupCreateResult toCreateResult(Group model) {
        if (model == null) return null;
        return GroupCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static GroupUpdateResult toUpdateResult(Group model) {
        if (model == null) return null;
        return GroupUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static GroupGetResult toGetResult(Group model) {
        if (model == null) return null;
        return GroupGetResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }
}
