package com.linhtn.authory_service.representation.rest.controller;

import com.linhtn.authory_service.config.security.model.PrincipalProfile;
import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.modules.auth.domain.dto.request.LoginRequest;
import com.linhtn.authory_service.modules.auth.domain.dto.request.RegisterRequest;
import com.linhtn.authory_service.modules.auth.domain.model.Auth;
import com.linhtn.authory_service.modules.auth.service.UserAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserAuthenticationService userAuthenticationService;

    public AuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
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
    public void logout(
            @RequestHeader(value = AUTHORIZATION) String bearerToken
    ) {
        String token = bearerToken.replace("Bearer ", "");
        userAuthenticationService.logout(token);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<Auth> refreshToken(
            @RequestHeader(value = AUTHORIZATION) String bearerToken
    ) {
        String token = bearerToken.replace("Bearer ", "");
        return ResponseEntity.ok().body(userAuthenticationService.refreshToken(token));
    }

    @GetMapping("/principal/profile")
    public ResponseEntity<PrincipalProfile> getPrincipalProfile(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        PrincipalProfile principal = userAuthenticationService.getPrincipal(token);
        return ResponseEntity.ok(principal);
    }

    @GetMapping("/principal/authorities")
    public ResponseEntity<PrincipalAuthorities> getPrincipalAuthorities(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        PrincipalAuthorities authorities = userAuthenticationService.getAuthorities(token);
        return ResponseEntity.ok(authorities);
    }
}
