package com.linhtn.authory_service.modules.abac.application.mapper;

import com.linhtn.authory_service.modules.abac.application.dto.command.AttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.command.AttributeUpdateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeCreateResult;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeGetResult;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeUpdateResult;
import com.linhtn.authory_service.modules.abac.domain.model.Attribute;

public class AttributeApplicationMapper {
    private AttributeApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Attribute toDomain(AttributeCreateCommand command) {
        if (command == null) return null;
        return Attribute.builder()
                .key(command.getKey())
                .name(command.getName())
                .description(command.getDescription())
                .build();
    }

    public static void updateDomain(Attribute model, AttributeUpdateCommand command) {
        if (model == null || command == null) return;
        if (command.getKey() != null) model.setKey(command.getKey());
        if (command.getName() != null) model.setName(command.getName());
        if (command.getDescription() != null) model.setDescription(command.getDescription());
    }

    public static AttributeCreateResult toCreateResult(Attribute model) {
        if (model == null) return null;
        return AttributeCreateResult.builder()
                .id(model.getId())
                .key(model.getKey())
                .name(model.getName())
                .description(model.getDescription())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static AttributeUpdateResult toUpdateResult(Attribute model) {
        if (model == null) return null;
        return AttributeUpdateResult.builder()
                .id(model.getId())
                .key(model.getKey())
                .name(model.getName())
                .description(model.getDescription())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static AttributeGetResult toGetResult(Attribute model) {
        if (model == null) return null;
        return AttributeGetResult.builder()
                .id(model.getId())
                .key(model.getKey())
                .name(model.getName())
                .description(model.getDescription())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }
}
