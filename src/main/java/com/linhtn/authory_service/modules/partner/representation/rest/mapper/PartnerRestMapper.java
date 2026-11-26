package com.linhtn.authory_service.modules.partner.representation.rest.mapper;

import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerCreateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerUpdateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerCreateResult;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerUpdateResult;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.request.PartnerCreateRequest;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.request.PartnerUpdateRequest;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.response.PartnerCreateResponse;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.response.PartnerGetResponse;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.response.PartnerUpdateResponse;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public class PartnerRestMapper {

    private PartnerRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static PartnerCreateCommand toCommand(PartnerCreateRequest request) {
        if (request == null) return null;
        return PartnerCreateCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();
    }

    public static PartnerUpdateCommand toCommand(PartnerUpdateRequest request) {
        if (request == null) return null;
        return PartnerUpdateCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();
    }

    public static PartnerCreateResponse toCreateResponse(PartnerCreateResult result) {
        if (result == null) return null;
        return PartnerCreateResponse.builder()
                .id(result.getId())
                .username(result.getUsername())
                .email(result.getEmail())
                .name(result.getName())
                .phone(result.getPhone())
                .address(result.getAddress())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PartnerUpdateResponse toUpdateResponse(PartnerUpdateResult result) {
        if (result == null) return null;
        return PartnerUpdateResponse.builder()
                .id(result.getId())
                .username(result.getUsername())
                .email(result.getEmail())
                .name(result.getName())
                .phone(result.getPhone())
                .address(result.getAddress())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PartnerGetResponse toGetResponse(PartnerGetResult result) {
        if (result == null) return null;
        return PartnerGetResponse.builder()
                .id(result.getId())
                .username(result.getUsername())
                .email(result.getEmail())
                .name(result.getName())
                .phone(result.getPhone())
                .address(result.getAddress())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .updatedAt(result.getUpdatedAt())
                .updatedBy(result.getUpdatedBy())
                .build();
    }

    public static PagingResponse<PartnerGetResponse> toGetResponse(
            PagingResponse<PartnerGetResult> page) {
        if (page == null) return null;
        return page.map(PartnerRestMapper::toGetResponse);
    }
}
