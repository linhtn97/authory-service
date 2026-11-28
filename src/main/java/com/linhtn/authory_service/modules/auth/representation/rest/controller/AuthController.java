package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.auth.application.dto.command.UserLoginCommand;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.LoginRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.RefreshTokenRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.AuthLoginResponse;
import com.linhtn.authory_service.modules.auth.representation.rest.mapper.AuthRestMapper;
import com.linhtn.security_core.annotation.PublicEndpoint;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Authentication & Tokens",
        description = "User login with credentials, token refreshing, and token revocation")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserAuthenticationUseCase userAuthenticationUseCase;

    public AuthController(UserAuthenticationUseCase userAuthenticationUseCase) {
        this.userAuthenticationUseCase = userAuthenticationUseCase;
    }

    @Operation(
            summary = "User login with credentials",
            description =
                    "Authenticates with username and password. Supports brute-force lockout defense after 5 consecutive failed attempts. "
                            + "If MFA is enabled, returns a temporary mfaToken for second-factor verification.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account locked for 15 minutes"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded (60 req/min)")
    })
    @PostMapping("/login")
    @PublicEndpoint
    public ResponseEntity<AuthLoginResponse> login(
            @Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        UserLoginCommand command = AuthRestMapper.toCommand(request);
        command.setIpAddress(getClientIp(httpRequest));
        command.setUserAgent(httpRequest.getHeader("User-Agent"));

        var result = userAuthenticationUseCase.login(command);
        return ResponseEntity.ok(AuthRestMapper.toResponse(result));
    }

    @Operation(
            summary = "Refresh access token",
            description =
                    "Exchanges a valid refresh token for a fresh pair of access and refresh tokens. Accepts refresh token via Authorization header or JSON request body.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
        @ApiResponse(responseCode = "401", description = "Refresh token expired or revoked")
    })
    @PostMapping("/refresh-token")
    @PublicEndpoint
    public ResponseEntity<AuthLoginResponse> refreshToken(
            @Parameter(description = "Bearer refresh token")
                    @RequestHeader(value = AUTHORIZATION, required = false)
                    String bearerToken,
            @RequestBody(required = false) RefreshTokenRequest request) {
        String token = null;
        if (bearerToken != null && !bearerToken.isBlank()) {
            token = bearerToken.replace("Bearer ", "");
        } else if (request != null
                && request.getRefreshToken() != null
                && !request.getRefreshToken().isBlank()) {
            token = request.getRefreshToken();
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Refresh token is required via Authorization header or request body");
        }
        var result = userAuthenticationUseCase.refreshToken(token);
        return ResponseEntity.ok(AuthRestMapper.toResponse(result));
    }

    @Operation(
            summary = "Logout user",
            description = "Revokes the active refresh token session for the caller.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Logged out successfully")})
    @PostMapping("/logout")
    @RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
    public ResponseEntity<Void> logout(
            @Parameter(description = "Bearer access token", required = true)
                    @RequestHeader(value = AUTHORIZATION)
                    String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        userAuthenticationUseCase.logout(token);
        return ResponseEntity.noContent().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
