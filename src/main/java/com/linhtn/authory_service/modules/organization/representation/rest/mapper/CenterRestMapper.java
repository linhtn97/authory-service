package com.linhtn.authory_service.modules.organization.representation.rest.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.CenterCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.CenterUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterUpdateResult;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.CenterCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.CenterUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CenterCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CenterGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CenterUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class CenterRestMapper {

    private CenterRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static CenterCreateCommand toCommand(CenterCreateRequest request) {
        if (request == null) return null;
        return CenterCreateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .divisionId(request.getDivisionId())
                .build();
    }

    public static CenterUpdateCommand toCommand(CenterUpdateRequest request) {
        if (request == null) return null;
        return CenterUpdateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .divisionId(request.getDivisionId())
                .build();
    }

    public static CenterCreateResponse toCreateResponse(CenterCreateResult result) {
        if (result == null) return null;
        return CenterCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .divisionId(result.getDivisionId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static CenterUpdateResponse toUpdateResponse(CenterUpdateResult result) {
        if (result == null) return null;
        return CenterUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .divisionId(result.getDivisionId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static CenterGetResponse toGetResponse(CenterGetResult result) {
        if (result == null) return null;
        return CenterGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .divisionId(result.getDivisionId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<CenterGetResponse> toGetResponse(
            PagingResponse<CenterGetResult> page) {
        if (page == null) return null;
        return page.map(CenterRestMapper::toGetResponse);
    }
}
