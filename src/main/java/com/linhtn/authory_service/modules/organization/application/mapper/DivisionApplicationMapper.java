package com.linhtn.authory_service.modules.organization.application.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.DivisionCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.DivisionUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionUpdateResult;
import com.linhtn.authory_service.modules.organization.domain.model.Division;

public class DivisionApplicationMapper {

    private DivisionApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Division toDomain(DivisionCreateCommand command) {
        if (command == null) {
            return null;
        }
        var builder = Division.builder().code(command.getCode()).name(command.getName());
        var model = builder.build();
        return model;
    }

    public static void updateDomain(Division model, DivisionUpdateCommand command) {
        if (model == null || command == null) {
            return;
        }
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
    }

    public static DivisionCreateResult toCreateResult(Division model) {
        if (model == null) return null;
        return DivisionCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static DivisionUpdateResult toUpdateResult(Division model) {
        if (model == null) return null;
        return DivisionUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static DivisionGetResult toGetResult(Division model) {
        if (model == null) return null;
        return DivisionGetResult.builder()
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
