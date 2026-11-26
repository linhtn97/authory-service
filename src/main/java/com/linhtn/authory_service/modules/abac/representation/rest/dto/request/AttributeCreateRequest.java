package com.linhtn.authory_service.modules.abac.representation.rest.dto.request;

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
public class AttributeCreateRequest {
    @NotBlank private String key;
    @NotBlank private String name;
    private String description;
}
