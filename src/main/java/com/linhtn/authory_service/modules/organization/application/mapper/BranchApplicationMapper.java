package com.linhtn.authory_service.modules.organization.application.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.BranchCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.BranchUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.BranchCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.BranchGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.BranchUpdateResult;
import com.linhtn.authory_service.modules.organization.domain.model.Branch;

public class BranchApplicationMapper {

    private BranchApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Branch toDomain(BranchCreateCommand command) {
        if (command == null) {
            return null;
        }
        var builder = Branch.builder().code(command.getCode()).name(command.getName());
        var model = builder.build();
        model.setCompanyId(command.getCompanyId());
        return model;
    }

    public static void updateDomain(Branch model, BranchUpdateCommand command) {
        if (model == null || command == null) {
            return;
        }
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
        model.setCompanyId(command.getCompanyId());
    }

    public static BranchCreateResult toCreateResult(Branch model) {
        if (model == null) return null;
        return BranchCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .companyId(model.getCompanyId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static BranchUpdateResult toUpdateResult(Branch model) {
        if (model == null) return null;
        return BranchUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .companyId(model.getCompanyId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static BranchGetResult toGetResult(Branch model) {
        if (model == null) return null;
        return BranchGetResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .companyId(model.getCompanyId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }
}
