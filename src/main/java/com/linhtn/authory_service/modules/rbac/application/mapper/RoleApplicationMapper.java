package com.linhtn.authory_service.modules.rbac.application.mapper;

import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleCreateResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleUpdateResult;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;

public class RoleApplicationMapper {

    private RoleApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Role toDomain(RoleCreateCommand command) {
        if (command == null) return null;
        return Role.builder().code(command.getCode()).name(command.getName()).build();
    }

    public static void updateDomain(Role model, RoleUpdateCommand command) {
        if (model == null || command == null) return;
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
    }

    public static RoleCreateResult toCreateResult(Role model) {
        if (model == null) return null;
        return RoleCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static RoleUpdateResult toUpdateResult(Role model) {
        if (model == null) return null;
        return RoleUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static RoleGetResult toGetResult(Role model) {
        if (model == null) return null;
        return RoleGetResult.builder()
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
