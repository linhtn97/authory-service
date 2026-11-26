package com.linhtn.authory_service.modules.rbac.representation.rest.mapper;

import com.linhtn.authory_service.modules.rbac.application.dto.command.GroupCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.command.GroupUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupCreateResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupUpdateResult;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.GroupCreateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.GroupUpdateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.GroupCreateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.GroupGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.GroupUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class GroupRestMapper {

    private GroupRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static GroupCreateCommand toCommand(GroupCreateRequest request) {
        if (request == null) return null;
        return GroupCreateCommand.builder().code(request.getCode()).name(request.getName()).build();
    }

    public static GroupUpdateCommand toCommand(GroupUpdateRequest request) {
        if (request == null) return null;
        return GroupUpdateCommand.builder().code(request.getCode()).name(request.getName()).build();
    }

    public static GroupCreateResponse toCreateResponse(GroupCreateResult result) {
        if (result == null) return null;
        return GroupCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static GroupUpdateResponse toUpdateResponse(GroupUpdateResult result) {
        if (result == null) return null;
        return GroupUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static GroupGetResponse toGetResponse(GroupGetResult result) {
        if (result == null) return null;
        return GroupGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<GroupGetResponse> toGetResponse(
            PagingResponse<GroupGetResult> page) {
        if (page == null) return null;
        return page.map(GroupRestMapper::toGetResponse);
    }
}
