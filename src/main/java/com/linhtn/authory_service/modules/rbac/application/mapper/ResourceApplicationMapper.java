package com.linhtn.authory_service.modules.rbac.application.mapper;

import com.linhtn.authory_service.modules.rbac.application.dto.command.ResourceCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.command.ResourceUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceCreateResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceUpdateResult;
import com.linhtn.authory_service.modules.rbac.domain.model.Resource;

public class ResourceApplicationMapper {

    private ResourceApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Resource toDomain(ResourceCreateCommand command) {
        if (command == null) return null;
        var builder = Resource.builder().code(command.getCode()).name(command.getName());
        var model = builder.build();
        return model;
    }

    public static void updateDomain(Resource model, ResourceUpdateCommand command) {
        if (model == null || command == null) return;
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
    }

    public static ResourceCreateResult toCreateResult(Resource model) {
        if (model == null) return null;
        return ResourceCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static ResourceUpdateResult toUpdateResult(Resource model) {
        if (model == null) return null;
        return ResourceUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static ResourceGetResult toGetResult(Resource model) {
        if (model == null) return null;
        return ResourceGetResult.builder()
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
