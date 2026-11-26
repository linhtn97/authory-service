package com.linhtn.authory_service.modules.rbac.domain.event;

import com.linhtn.authory_service.modules.rbac.domain.model.Group;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupCreatedEvent {
    private final Group group;
}
