package com.linhtn.authory_service.modules.user.representation.rest.mapper;

import com.linhtn.authory_service.modules.user.application.dto.command.UserCreateCommand;
import com.linhtn.authory_service.modules.user.application.dto.command.UserUpdateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserCreateResult;
import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.dto.result.UserUpdateResult;
import com.linhtn.authory_service.modules.user.representation.rest.dto.request.UserCreateRequest;
import com.linhtn.authory_service.modules.user.representation.rest.dto.request.UserUpdateRequest;
import com.linhtn.authory_service.modules.user.representation.rest.dto.response.UserCreateResponse;
import com.linhtn.authory_service.modules.user.representation.rest.dto.response.UserGetResponse;
import com.linhtn.authory_service.modules.user.representation.rest.dto.response.UserUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class UserRestMapper {

    private UserRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static UserCreateCommand toCommand(UserCreateRequest request) {
        if (request == null) return null;
        return UserCreateCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    public static UserUpdateCommand toCommand(UserUpdateRequest request) {
        if (request == null) return null;
        return UserUpdateCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    public static UserCreateResponse toCreateResponse(UserCreateResult result) {
        if (result == null) return null;
        return UserCreateResponse.builder()
                .id(result.getId())
                .username(result.getUsername())
                .email(result.getEmail())
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .version(result.getVersion())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static UserUpdateResponse toUpdateResponse(UserUpdateResult result) {
        if (result == null) return null;
        return UserUpdateResponse.builder()
                .id(result.getId())
                .username(result.getUsername())
                .email(result.getEmail())
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .version(result.getVersion())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static UserGetResponse toGetResponse(UserGetResult result) {
        if (result == null) return null;
        return UserGetResponse.builder()
                .id(result.getId())
                .username(result.getUsername())
                .email(result.getEmail())
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .version(result.getVersion())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<UserGetResponse> toGetResponse(
            PagingResponse<UserGetResult> page) {
        if (page == null) return null;
        return page.map(UserRestMapper::toGetResponse);
    }
}
