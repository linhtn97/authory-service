package com.linhtn.authory_service.modules.user.service;

import com.linhtn.authory_service.modules.user.domain.dto.UserDto;
import com.linhtn.shared_kernel.domain.service.BaseCrudService;

public interface UserService extends BaseCrudService<UserDto> {
    UserDto getByUsername(String username);

    UserDto getByEmail(String email);

    UserDto getByUsernameOrEmail(String um);

    boolean existsByUsernameOrEmail(String username, String email);
}
