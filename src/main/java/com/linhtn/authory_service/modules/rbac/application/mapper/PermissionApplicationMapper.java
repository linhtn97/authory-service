package com.linhtn.authory_service.modules.rbac.application.mapper;

import com.linhtn.authory_service.modules.rbac.application.dto.command.PermissionCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.command.PermissionUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionCreateResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionUpdateResult;
import com.linhtn.authory_service.modules.rbac.domain.model.Permission;

public class PermissionApplicationMapper {

    private PermissionApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Permission toDomain(PermissionCreateCommand command) {
        if (command == null) return null;
        var builder = Permission.builder().code(command.getCode()).name(command.getName());
        var model = builder.build();
        model.setAction(command.getAction());
        model.setType(command.getType());
        return model;
    }

    public static void updateDomain(Permission model, PermissionUpdateCommand command) {
        if (model == null || command == null) return;
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
        model.setAction(command.getAction());
        model.setType(command.getType());
    }

    public static PermissionCreateResult toCreateResult(Permission model) {
        if (model == null) return null;
        return PermissionCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .action(model.getAction())
                .type(model.getType())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static PermissionUpdateResult toUpdateResult(Permission model) {
        if (model == null) return null;
        return PermissionUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .action(model.getAction())
                .type(model.getType())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static PermissionGetResult toGetResult(Permission model) {
        if (model == null) return null;
        return PermissionGetResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .action(model.getAction())
                .type(model.getType())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }
}
