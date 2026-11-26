package com.linhtn.authory_service.modules.organization.application.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.DepartmentCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.DepartmentUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentUpdateResult;
import com.linhtn.authory_service.modules.organization.domain.model.Department;

public class DepartmentApplicationMapper {

    private DepartmentApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Department toDomain(DepartmentCreateCommand command) {
        if (command == null) {
            return null;
        }
        var builder = Department.builder().code(command.getCode()).name(command.getName());
        var model = builder.build();
        model.setBranchId(command.getBranchId());
        model.setCenterId(command.getCenterId());
        return model;
    }

    public static void updateDomain(Department model, DepartmentUpdateCommand command) {
        if (model == null || command == null) {
            return;
        }
        if (command.getCode() != null) model.setCode(command.getCode());
        if (command.getName() != null) model.setName(command.getName());
        model.setBranchId(command.getBranchId());
        model.setCenterId(command.getCenterId());
    }

    public static DepartmentCreateResult toCreateResult(Department model) {
        if (model == null) return null;
        return DepartmentCreateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .branchId(model.getBranchId())
                .centerId(model.getCenterId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static DepartmentUpdateResult toUpdateResult(Department model) {
        if (model == null) return null;
        return DepartmentUpdateResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .branchId(model.getBranchId())
                .centerId(model.getCenterId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static DepartmentGetResult toGetResult(Department model) {
        if (model == null) return null;
        return DepartmentGetResult.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .branchId(model.getBranchId())
                .centerId(model.getCenterId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }
}
