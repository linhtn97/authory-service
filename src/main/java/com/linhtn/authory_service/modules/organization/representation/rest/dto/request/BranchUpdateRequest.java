package com.linhtn.authory_service.modules.organization.representation.rest.dto.request;

import java.util.UUID;

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
public class BranchUpdateRequest {
    private String code;
    private String name;
    private UUID companyId;
}
