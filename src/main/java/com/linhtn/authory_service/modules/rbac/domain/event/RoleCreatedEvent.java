package com.linhtn.authory_service.modules.rbac.domain.event;

import com.linhtn.authory_service.modules.rbac.domain.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleCreatedEvent {
    private final Role role;
}
