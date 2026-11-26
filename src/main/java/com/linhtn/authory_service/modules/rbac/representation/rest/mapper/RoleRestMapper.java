package com.linhtn.authory_service.modules.rbac.representation.rest.mapper;

import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleCreateResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleUpdateResult;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.RoleCreateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.RoleUpdateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.RoleCreateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.RoleGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.RoleUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class RoleRestMapper {

    private RoleRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static RoleCreateCommand toCommand(RoleCreateRequest request) {
        if (request == null) return null;
        return RoleCreateCommand.builder().code(request.getCode()).name(request.getName()).build();
    }

    public static RoleUpdateCommand toCommand(RoleUpdateRequest request) {
        if (request == null) return null;
        return RoleUpdateCommand.builder().code(request.getCode()).name(request.getName()).build();
    }

    public static RoleCreateResponse toCreateResponse(RoleCreateResult result) {
        if (result == null) return null;
        return RoleCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static RoleUpdateResponse toUpdateResponse(RoleUpdateResult result) {
        if (result == null) return null;
        return RoleUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static RoleGetResponse toGetResponse(RoleGetResult result) {
        if (result == null) return null;
        return RoleGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<RoleGetResponse> toGetResponse(
            PagingResponse<RoleGetResult> page) {
        if (page == null) return null;
        return page.map(RoleRestMapper::toGetResponse);
    }
}
