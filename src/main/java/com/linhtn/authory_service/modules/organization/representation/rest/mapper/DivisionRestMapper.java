package com.linhtn.authory_service.modules.organization.representation.rest.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.DivisionCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.DivisionUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionUpdateResult;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.DivisionCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.DivisionUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DivisionCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DivisionGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DivisionUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class DivisionRestMapper {

    private DivisionRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static DivisionCreateCommand toCommand(DivisionCreateRequest request) {
        if (request == null) return null;
        return DivisionCreateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .build();
    }

    public static DivisionUpdateCommand toCommand(DivisionUpdateRequest request) {
        if (request == null) return null;
        return DivisionUpdateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .build();
    }

    public static DivisionCreateResponse toCreateResponse(DivisionCreateResult result) {
        if (result == null) return null;
        return DivisionCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static DivisionUpdateResponse toUpdateResponse(DivisionUpdateResult result) {
        if (result == null) return null;
        return DivisionUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static DivisionGetResponse toGetResponse(DivisionGetResult result) {
        if (result == null) return null;
        return DivisionGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<DivisionGetResponse> toGetResponse(
            PagingResponse<DivisionGetResult> page) {
        if (page == null) return null;
        return page.map(DivisionRestMapper::toGetResponse);
    }
}
