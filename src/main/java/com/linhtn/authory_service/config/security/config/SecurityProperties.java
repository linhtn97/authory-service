package com.linhtn.authory_service.config.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

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
