package com.linhtn.authory_service.modules.rbac.representation.rest.mapper;

import com.linhtn.authory_service.modules.rbac.application.dto.command.ResourceCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.command.ResourceUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceCreateResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceUpdateResult;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.ResourceCreateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.ResourceUpdateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.ResourceCreateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.ResourceGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.ResourceUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class ResourceRestMapper {

    private ResourceRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ResourceCreateCommand toCommand(ResourceCreateRequest request) {
        if (request == null) return null;
        return ResourceCreateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .build();
    }

    public static ResourceUpdateCommand toCommand(ResourceUpdateRequest request) {
        if (request == null) return null;
        return ResourceUpdateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .build();
    }

    public static ResourceCreateResponse toCreateResponse(ResourceCreateResult result) {
        if (result == null) return null;
        return ResourceCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static ResourceUpdateResponse toUpdateResponse(ResourceUpdateResult result) {
        if (result == null) return null;
        return ResourceUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static ResourceGetResponse toGetResponse(ResourceGetResult result) {
        if (result == null) return null;
        return ResourceGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<ResourceGetResponse> toGetResponse(
            PagingResponse<ResourceGetResult> page) {
        if (page == null) return null;
        return page.map(ResourceRestMapper::toGetResponse);
    }
}
