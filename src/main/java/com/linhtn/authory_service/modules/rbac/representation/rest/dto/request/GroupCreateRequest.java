package com.linhtn.authory_service.modules.rbac.representation.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateRequest {
    @NotBlank private String code;
    @NotBlank private String name;
}
