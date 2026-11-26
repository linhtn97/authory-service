package com.linhtn.authory_service.modules.organization.representation.rest.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.DepartmentCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.DepartmentUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentUpdateResult;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.DepartmentCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.DepartmentUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DepartmentCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DepartmentGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DepartmentUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class DepartmentRestMapper {

    private DepartmentRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static DepartmentCreateCommand toCommand(DepartmentCreateRequest request) {
        if (request == null) return null;
        return DepartmentCreateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .branchId(request.getBranchId())
                .centerId(request.getCenterId())
                .build();
    }

    public static DepartmentUpdateCommand toCommand(DepartmentUpdateRequest request) {
        if (request == null) return null;
        return DepartmentUpdateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .branchId(request.getBranchId())
                .centerId(request.getCenterId())
                .build();
    }

    public static DepartmentCreateResponse toCreateResponse(DepartmentCreateResult result) {
        if (result == null) return null;
        return DepartmentCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .branchId(result.getBranchId())
                .centerId(result.getCenterId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static DepartmentUpdateResponse toUpdateResponse(DepartmentUpdateResult result) {
        if (result == null) return null;
        return DepartmentUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .branchId(result.getBranchId())
                .centerId(result.getCenterId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static DepartmentGetResponse toGetResponse(DepartmentGetResult result) {
        if (result == null) return null;
        return DepartmentGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .branchId(result.getBranchId())
                .centerId(result.getCenterId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<DepartmentGetResponse> toGetResponse(
            PagingResponse<DepartmentGetResult> page) {
        if (page == null) return null;
        return page.map(DepartmentRestMapper::toGetResponse);
    }
}
