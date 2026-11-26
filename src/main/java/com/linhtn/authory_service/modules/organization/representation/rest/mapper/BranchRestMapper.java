package com.linhtn.authory_service.modules.organization.representation.rest.mapper;

import com.linhtn.authory_service.modules.organization.application.dto.command.BranchCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.command.BranchUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.BranchCreateResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.BranchGetResult;
import com.linhtn.authory_service.modules.organization.application.dto.result.BranchUpdateResult;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.BranchCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.BranchUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.BranchCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.BranchGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.BranchUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class BranchRestMapper {

    private BranchRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static BranchCreateCommand toCommand(BranchCreateRequest request) {
        if (request == null) return null;
        return BranchCreateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .companyId(request.getCompanyId())
                .build();
    }

    public static BranchUpdateCommand toCommand(BranchUpdateRequest request) {
        if (request == null) return null;
        return BranchUpdateCommand.builder()
                .code(request.getCode())
                .name(request.getName())
                .companyId(request.getCompanyId())
                .build();
    }

    public static BranchCreateResponse toCreateResponse(BranchCreateResult result) {
        if (result == null) return null;
        return BranchCreateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .companyId(result.getCompanyId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static BranchUpdateResponse toUpdateResponse(BranchUpdateResult result) {
        if (result == null) return null;
        return BranchUpdateResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .companyId(result.getCompanyId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static BranchGetResponse toGetResponse(BranchGetResult result) {
        if (result == null) return null;
        return BranchGetResponse.builder()
                .id(result.getId())
                .code(result.getCode())
                .name(result.getName())
                .companyId(result.getCompanyId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<BranchGetResponse> toGetResponse(
            PagingResponse<BranchGetResult> page) {
        if (page == null) return null;
        return page.map(BranchRestMapper::toGetResponse);
    }
}
