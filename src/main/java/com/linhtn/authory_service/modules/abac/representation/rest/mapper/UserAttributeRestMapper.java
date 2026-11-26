package com.linhtn.authory_service.modules.abac.representation.rest.mapper;

import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeUpdateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeCreateResult;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeGetResult;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeUpdateResult;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.request.UserAttributeCreateRequest;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.request.UserAttributeUpdateRequest;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.UserAttributeCreateResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.UserAttributeGetResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.UserAttributeUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class UserAttributeRestMapper {

    private UserAttributeRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static UserAttributeCreateCommand toCommand(UserAttributeCreateRequest request) {
        if (request == null) return null;
        return UserAttributeCreateCommand.builder()
                .key(request.getKey())
                .value(request.getValue())
                .userId(request.getUserId())
                .build();
    }

    public static UserAttributeUpdateCommand toCommand(UserAttributeUpdateRequest request) {
        if (request == null) return null;
        return UserAttributeUpdateCommand.builder()
                .key(request.getKey())
                .value(request.getValue())
                .userId(request.getUserId())
                .build();
    }

    public static UserAttributeCreateResponse toCreateResponse(UserAttributeCreateResult result) {
        if (result == null) return null;
        return UserAttributeCreateResponse.builder()
                .id(result.getId())
                .key(result.getKey())
                .value(result.getValue())
                .userId(result.getUserId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static UserAttributeUpdateResponse toUpdateResponse(UserAttributeUpdateResult result) {
        if (result == null) return null;
        return UserAttributeUpdateResponse.builder()
                .id(result.getId())
                .key(result.getKey())
                .value(result.getValue())
                .userId(result.getUserId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static UserAttributeGetResponse toGetResponse(UserAttributeGetResult result) {
        if (result == null) return null;
        return UserAttributeGetResponse.builder()
                .id(result.getId())
                .key(result.getKey())
                .value(result.getValue())
                .userId(result.getUserId())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<UserAttributeGetResponse> toGetResponse(
            PagingResponse<UserAttributeGetResult> page) {
        if (page == null) return null;
        return page.map(UserAttributeRestMapper::toGetResponse);
    }
}
