package com.linhtn.authory_service.config.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linhtn.authory_service.config.security.util.CurrentPrincipalUtil;
import com.linhtn.security_core.model.AuthenticatedUser;
import com.linhtn.security_core.port.UserContextProvider;

/**
 * Enterprise bridge connecting authory-service security context to the platform {@link
 * AuthenticatedUser} context for declarative authorization.
 */
@Configuration
public class SecurityCoreBridgeConfig {

    @Bean
    public UserContextProvider userContextProvider() {
        return CurrentPrincipalUtil::getPrincipal;
    }
}
