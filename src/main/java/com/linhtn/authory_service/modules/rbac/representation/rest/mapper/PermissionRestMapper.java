package com.linhtn.authory_service.modules.rbac.representation.rest.mapper;

import com.linhtn.authory_service.modules.rbac.application.dto.command.PermissionCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.command.PermissionUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionCreateResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionUpdateResult;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.PermissionCreateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.PermissionUpdateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.PermissionCreateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.PermissionGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.PermissionUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class PermissionRestMapper {

    private PermissionRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static PermissionCreateCommand toCommand(PermissionCreateRequest request) {
        if (request == null) return null;
        return PermissionCreateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .action(request.getAction())
                .type(request.getType())
                .build();
    }

    public static PermissionUpdateCommand toCommand(PermissionUpdateRequest request) {
        if (request == null) return null;
        return PermissionUpdateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .action(request.getAction())
                .type(request.getType())
                .build();
    }

    public static PermissionCreateResponse toCreateResponse(PermissionCreateResult result) {
        if (result == null) return null;
        return PermissionCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .action(result.getAction())
                .type(result.getType())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PermissionUpdateResponse toUpdateResponse(PermissionUpdateResult result) {
        if (result == null) return null;
        return PermissionUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .action(result.getAction())
                .type(result.getType())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PermissionGetResponse toGetResponse(PermissionGetResult result) {
        if (result == null) return null;
        return PermissionGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .action(result.getAction())
                .type(result.getType())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<PermissionGetResponse> toGetResponse(
            PagingResponse<PermissionGetResult> page) {
        if (page == null) return null;
        return page.map(PermissionRestMapper::toGetResponse);
    }
}
