package com.linhtn.authory_service.modules.user.domain.event;

import com.linhtn.authory_service.modules.user.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateEvent {
    private final User user;
}
