package com.linhtn.authory_service.modules.organization.representation.rest.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.PositionCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.PositionUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionUpdateResult;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.PositionCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.PositionUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.PositionCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.PositionGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.PositionUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class PositionRestMapper {

    private PositionRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static PositionCreateCommand toCommand(PositionCreateRequest request) {
        if (request == null) return null;
        return PositionCreateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .build();
    }

    public static PositionUpdateCommand toCommand(PositionUpdateRequest request) {
        if (request == null) return null;
        return PositionUpdateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .build();
    }

    public static PositionCreateResponse toCreateResponse(PositionCreateResult result) {
        if (result == null) return null;
        return PositionCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PositionUpdateResponse toUpdateResponse(PositionUpdateResult result) {
        if (result == null) return null;
        return PositionUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PositionGetResponse toGetResponse(PositionGetResult result) {
        if (result == null) return null;
        return PositionGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<PositionGetResponse> toGetResponse(
            PagingResponse<PositionGetResult> page) {
        if (page == null) return null;
        return page.map(PositionRestMapper::toGetResponse);
    }
}
