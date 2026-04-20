package com.linhtn.authory_service.config.security.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "authory-server-impl")
public class SecurityProperties {
    private List<String> whitelistRoutes;
    private List<String> swaggerRoutes;
    private JwtConfig jwt;

    @Getter
    @Setter
    public static class JwtConfig {
        private String secretKey;

        private long accessExpiration;

        private long refreshExpiration;
    }
}
