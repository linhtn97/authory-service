package com.linhtn.authory_service.modules.user.domain.event;

import com.linhtn.authory_service.modules.user.domain.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCreateEvent {
    private final User user;
}
