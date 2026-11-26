package com.linhtn.authory_service.modules.partner.application.mapper;

import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerCreateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerUpdateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerCreateResult;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerUpdateResult;
import com.linhtn.authory_service.modules.partner.domain.model.Partner;

public class PartnerApplicationMapper {

    private PartnerApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Partner toDomain(PartnerCreateCommand command) {
        if (command == null) {
            return null;
        }
        return Partner.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .name(command.getName())
                .phone(command.getPhone())
                .address(command.getAddress())
                .build();
    }

    public static void updateDomain(Partner partner, PartnerUpdateCommand command) {
        if (partner == null || command == null) {
            return;
        }
        partner.setUsername(command.getUsername());
        partner.setEmail(command.getEmail());
        partner.setName(command.getName());
        partner.setPhone(command.getPhone());
        partner.setAddress(command.getAddress());
    }

    public static PartnerCreateResult toCreateResult(Partner partner) {
        if (partner == null) return null;
        return PartnerCreateResult.builder()
                .id(partner.getId())
                .username(partner.getUsername())
                .email(partner.getEmail())
                .name(partner.getName())
                .phone(partner.getPhone())
                .address(partner.getAddress())
                .createdAt(partner.getCreatedAt())
                .createdBy(partner.getCreatedBy())
                .updatedAt(partner.getUpdatedAt())
                .updatedBy(partner.getUpdatedBy())
                .build();
    }

    public static PartnerUpdateResult toUpdateResult(Partner partner) {
        if (partner == null) return null;
        return PartnerUpdateResult.builder()
                .id(partner.getId())
                .username(partner.getUsername())
                .email(partner.getEmail())
                .name(partner.getName())
                .phone(partner.getPhone())
                .address(partner.getAddress())
                .createdAt(partner.getCreatedAt())
                .createdBy(partner.getCreatedBy())
                .updatedAt(partner.getUpdatedAt())
                .updatedBy(partner.getUpdatedBy())
                .build();
    }

    public static PartnerGetResult toGetResult(Partner partner) {
        if (partner == null) return null;
        return PartnerGetResult.builder()
                .id(partner.getId())
                .username(partner.getUsername())
                .email(partner.getEmail())
                .name(partner.getName())
                .phone(partner.getPhone())
                .address(partner.getAddress())
                .createdAt(partner.getCreatedAt())
                .createdBy(partner.getCreatedBy())
                .updatedAt(partner.getUpdatedAt())
                .updatedBy(partner.getUpdatedBy())
                .build();
    }
}
