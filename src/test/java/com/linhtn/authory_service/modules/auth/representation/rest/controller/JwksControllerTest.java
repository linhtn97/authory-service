package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.linhtn.authory_service.config.security.provider.RsaKeyManager;

class JwksControllerTest {

    @Test
    @DisplayName("Should return JWKS payload from RsaKeyManager")
    void shouldReturnJwksPayload() {
        RsaKeyManager rsaKeyManager = mock(RsaKeyManager.class);
        Map<String, Object> jwks =
                Map.of("keys", List.of(Map.of("kid", "authory-key-1", "kty", "RSA")));
        when(rsaKeyManager.getJwks()).thenReturn(jwks);

        JwksController controller = new JwksController(rsaKeyManager);
        ResponseEntity<Map<String, Object>> response = controller.getJwks();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(jwks);
    }
}
