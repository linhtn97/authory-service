package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.config.security.provider.RsaKeyManager;
import com.linhtn.security_core.annotation.PublicEndpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@PublicEndpoint
@Tag(name = "Authentication & JWKS", description = "JSON Web Key Set and public key discovery")
public class JwksController {

    private final RsaKeyManager rsaKeyManager;

    public JwksController(RsaKeyManager rsaKeyManager) {
        this.rsaKeyManager = rsaKeyManager;
    }

    @GetMapping(
            value = {"/api/v1/auth/jwks.json", "/.well-known/jwks.json"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get JSON Web Key Set (JWKS)",
            description = "Public key set for verifying RS256 signed JWT tokens.")
    public ResponseEntity<Map<String, Object>> getJwks() {
        return ResponseEntity.ok(rsaKeyManager.getJwks());
    }
}
