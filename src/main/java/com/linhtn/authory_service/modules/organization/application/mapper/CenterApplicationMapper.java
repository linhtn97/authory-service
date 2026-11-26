package com.linhtn.authory_service.modules.organization.application.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.CenterCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.CenterUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterUpdateResult;
import com.linhtn.authory_service.modules.organization.domain.model.Center;

public class CenterApplicationMapper {

    private CenterApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Center toDomain(CenterCreateCommand command) {
        if (command == null) {
            return null;
        }
        var builder = Center.builder().code(command.getCode()).name(command.getName());
        var model = builder.build();
        model.setDivisionId(command.getDivisionId());
        return model;
    }

    public static void updateDomain(Center model, CenterUpdateCommand command) {
        if (model == null || command == null) {
            return;
        }
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
        model.setDivisionId(command.getDivisionId());
    }

    public static CenterCreateResult toCreateResult(Center model) {
        if (model == null) return null;
        return CenterCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .divisionId(model.getDivisionId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static CenterUpdateResult toUpdateResult(Center model) {
        if (model == null) return null;
        return CenterUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .divisionId(model.getDivisionId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static CenterGetResult toGetResult(Center model) {
        if (model == null) return null;
        return CenterGetResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .divisionId(model.getDivisionId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }
}
