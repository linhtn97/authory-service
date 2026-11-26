package com.linhtn.authory_service.modules.organization.representation.rest.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.CompanyCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.CompanyUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyUpdateResult;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.CompanyCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.CompanyUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CompanyCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CompanyGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CompanyUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class CompanyRestMapper {

    private CompanyRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static CompanyCreateCommand toCommand(CompanyCreateRequest request) {
        if (request == null) return null;
        return CompanyCreateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .build();
    }

    public static CompanyUpdateCommand toCommand(CompanyUpdateRequest request) {
        if (request == null) return null;
        return CompanyUpdateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .build();
    }

    public static CompanyCreateResponse toCreateResponse(CompanyCreateResult result) {
        if (result == null) return null;
        return CompanyCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static CompanyUpdateResponse toUpdateResponse(CompanyUpdateResult result) {
        if (result == null) return null;
        return CompanyUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static CompanyGetResponse toGetResponse(CompanyGetResult result) {
        if (result == null) return null;
        return CompanyGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<CompanyGetResponse> toGetResponse(
            PagingResponse<CompanyGetResult> page) {
        if (page == null) return null;
        return page.map(CompanyRestMapper::toGetResponse);
    }
}
