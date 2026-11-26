package com.linhtn.authory_service.modules.organization.application.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.PositionCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.PositionUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionUpdateResult;
import com.linhtn.authory_service.modules.organization.domain.model.Position;

public class PositionApplicationMapper {

    private PositionApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Position toDomain(PositionCreateCommand command) {
        if (command == null) {
            return null;
        }
        var builder = Position.builder().code(command.getCode()).name(command.getName());
        var model = builder.build();
        return model;
    }

    public static void updateDomain(Position model, PositionUpdateCommand command) {
        if (model == null || command == null) {
            return;
        }
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
    }

    public static PositionCreateResult toCreateResult(Position model) {
        if (model == null) return null;
        return PositionCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static PositionUpdateResult toUpdateResult(Position model) {
        if (model == null) return null;
        return PositionUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static PositionGetResult toGetResult(Position model) {
        if (model == null) return null;
        return PositionGetResult.builder()
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
