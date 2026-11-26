package com.linhtn.authory_service.modules.abac.representation.rest.mapper;

import com.linhtn.authory_service.modules.abac.application.dto.command.AttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.command.AttributeUpdateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeCreateResult;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeGetResult;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeUpdateResult;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.request.AttributeCreateRequest;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.request.AttributeUpdateRequest;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.AttributeCreateResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.AttributeGetResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.AttributeUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class AttributeRestMapper {

    private AttributeRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static AttributeCreateCommand toCommand(AttributeCreateRequest request) {
        if (request == null) return null;
        return AttributeCreateCommand.builder()
                .key(request.getKey())
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public static AttributeUpdateCommand toCommand(AttributeUpdateRequest request) {
        if (request == null) return null;
        return AttributeUpdateCommand.builder()
                .key(request.getKey())
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public static AttributeCreateResponse toCreateResponse(AttributeCreateResult result) {
        if (result == null) return null;
        return AttributeCreateResponse.builder()
                .id(result.getId())
                .key(result.getKey())
                .name(result.getName())
                .description(result.getDescription())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static AttributeUpdateResponse toUpdateResponse(AttributeUpdateResult result) {
        if (result == null) return null;
        return AttributeUpdateResponse.builder()
                .id(result.getId())
                .key(result.getKey())
                .name(result.getName())
                .description(result.getDescription())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static AttributeGetResponse toGetResponse(AttributeGetResult result) {
        if (result == null) return null;
        return AttributeGetResponse.builder()
                .id(result.getId())
                .key(result.getKey())
                .name(result.getName())
                .description(result.getDescription())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<AttributeGetResponse> toGetResponse(
            PagingResponse<AttributeGetResult> page) {
        if (page == null) return null;
        return page.map(AttributeRestMapper::toGetResponse);
    }
}
