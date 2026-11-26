package com.linhtn.authory_service.modules.rbac.domain.event;

import com.linhtn.authory_service.modules.rbac.domain.model.Group;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupDeletedEvent {
    private final Group group;
}
