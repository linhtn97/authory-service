package com.linhtn.authory_service.modules.partner.application.dto.command;

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
public class PartnerUpdateCommand {
    private String username;
    private String email;
    private String name;
    private String phone;
    private String address;
}
