package com.linhtn.authory_service.modules.user.application.dto.command;

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
public class UserUpdateCommand {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
