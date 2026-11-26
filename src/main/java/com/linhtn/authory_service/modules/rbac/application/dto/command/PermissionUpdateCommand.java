package com.linhtn.authory_service.modules.rbac.application.dto.command;

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
public class PermissionUpdateCommand {
    private String code;
    private String name;
    private String action;
    private String type;
}
