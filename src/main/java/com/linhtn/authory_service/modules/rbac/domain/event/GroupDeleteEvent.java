package com.linhtn.authory_service.modules.rbac.domain.event;

import com.linhtn.authory_service.modules.rbac.domain.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupDeleteEvent {
    private final Group group;
}
