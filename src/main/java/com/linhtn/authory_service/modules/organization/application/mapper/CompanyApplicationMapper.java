package com.linhtn.authory_service.modules.organization.application.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.CompanyCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.CompanyUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyUpdateResult;
import com.linhtn.authory_service.modules.organization.domain.model.Company;

public class CompanyApplicationMapper {

    private CompanyApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Company toDomain(CompanyCreateCommand command) {
        if (command == null) {
            return null;
        }
        var builder = Company.builder().code(command.getCode()).name(command.getName());
        var model = builder.build();
        return model;
    }

    public static void updateDomain(Company model, CompanyUpdateCommand command) {
        if (model == null || command == null) {
            return;
        }
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
    }

    public static CompanyCreateResult toCreateResult(Company model) {
        if (model == null) return null;
        return CompanyCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static CompanyUpdateResult toUpdateResult(Company model) {
        if (model == null) return null;
        return CompanyUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static CompanyGetResult toGetResult(Company model) {
        if (model == null) return null;
        return CompanyGetResult.builder()
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
