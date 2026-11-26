package com.linhtn.authory_service.modules.abac.representation.rest.dto.request;

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
public class AttributeUpdateRequest {
    private String key;
    private String name;
    private String description;
}
