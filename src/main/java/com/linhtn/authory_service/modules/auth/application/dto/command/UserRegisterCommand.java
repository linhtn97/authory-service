package com.linhtn.authory_service.modules.auth.application.dto.command;

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
public class UserRegisterCommand {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
