package com.linhtn.authory_service.config.security.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.linhtn.authory_service.config.security.config.SecurityProperties;

class RsaKeyManagerTest {

    @Test
    @DisplayName(
            "Should auto-generate RSA key pair and produce valid RFC 7517 JWKS when RS256 configured")
    void shouldGenerateRsaKeyAndJwksWhenRs256() {
        SecurityProperties properties = new SecurityProperties();
        SecurityProperties.JwtConfig jwtConfig = new SecurityProperties.JwtConfig();
        jwtConfig.setAlgorithm("RS256");
        properties.setJwt(jwtConfig);

        RsaKeyManager manager = new RsaKeyManager(properties);

        assertThat(manager.isRsaEnabled()).isTrue();
        assertThat(manager.getPrivateKey()).isNotNull();
        assertThat(manager.getPublicKey()).isNotNull();

        Map<String, Object> jwks = manager.getJwks();
        assertThat(jwks).containsKey("keys");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> keys = (List<Map<String, Object>>) jwks.get("keys");
        assertThat(keys).hasSize(1);

        Map<String, Object> key = keys.get(0);
        assertThat(key.get("kty")).isEqualTo("RSA");
        assertThat(key.get("use")).isEqualTo("sig");
        assertThat(key.get("alg")).isEqualTo("RS256");
        assertThat(key.get("kid")).isEqualTo("authory-key-1");
        assertThat(key.get("n")).isNotNull();
        assertThat(key.get("e")).isEqualTo("AQAB");
    }

    @Test
    @DisplayName("Should remain disabled when algorithm is HS256")
    void shouldBeDisabledWhenHs256() {
        SecurityProperties properties = new SecurityProperties();
        SecurityProperties.JwtConfig jwtConfig = new SecurityProperties.JwtConfig();
        jwtConfig.setAlgorithm("HS256");
        properties.setJwt(jwtConfig);

        RsaKeyManager manager = new RsaKeyManager(properties);

        assertThat(manager.isRsaEnabled()).isFalse();
        assertThat(manager.getPrivateKey()).isNull();
        assertThat(manager.getPublicKey()).isNull();
        assertThat(manager.getJwks().get("keys")).asList().isEmpty();
    }
}
