package com.linhtn.authory_service.representation.rest.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.modules.auth.domain.dto.request.LoginRequest;
import com.linhtn.authory_service.modules.auth.domain.dto.request.RegisterRequest;
import com.linhtn.authory_service.modules.auth.domain.model.Auth;
import com.linhtn.authory_service.modules.auth.service.PartnerAuthenticationService;
import com.linhtn.authory_service.modules.auth.service.UserAuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserAuthenticationService userAuthenticationService;
    private final PartnerAuthenticationService partnerAuthenticationService;

    public AuthenticationController(UserAuthenticationService userAuthenticationService,
            PartnerAuthenticationService partnerAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
        this.partnerAuthenticationService = partnerAuthenticationService;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        userAuthenticationService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<Auth> login(@RequestBody LoginRequest request) {
        Auth auth = userAuthenticationService.login(request);
        return ResponseEntity.ok(auth);
    }

    @GetMapping("/logout")
    public void logout(@RequestHeader(value = AUTHORIZATION) String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        userAuthenticationService.logout(token);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<Auth> refreshToken(
            @RequestHeader(value = AUTHORIZATION) String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        return ResponseEntity.ok().body(userAuthenticationService.refreshToken(token));
    }

    @GetMapping("/principal")
    public ResponseEntity<Principal> getPrincipal(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Principal principal = userAuthenticationService.getPrincipal(token);
        return ResponseEntity.ok(principal);
    }

    @GetMapping("/principal/partner")
    public ResponseEntity<Principal> getPartnerPrincipal(
            @RequestHeader("Client-Id") String clientId,  @RequestHeader("Client-Key") String clientKey) {
        Principal principal = partnerAuthenticationService.getPrincipal(clientId, clientKey);
        return ResponseEntity.ok(principal);
    }

    @PostMapping("/issue-api-key")
    public ResponseEntity<Void> issueApiKey(@RequestParam UUID partnerId) {
        partnerAuthenticationService.issueApiKey(partnerId);
        return ResponseEntity.noContent().build();
    }
}
