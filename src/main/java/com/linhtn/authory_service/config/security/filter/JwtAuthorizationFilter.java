package com.linhtn.authory_service.config.security.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.linhtn.authory_service.config.security.config.SecurityProperties;
import com.linhtn.authory_service.config.security.exception.error.AuthError;
import com.linhtn.authory_service.config.security.provider.JwtProvider;
import com.linhtn.authory_service.modules.auth.application.port.in.TokenBlacklistUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.security_core.model.AuthenticatedUser;
import com.linhtn.sharedkernel.representation.ApiErrorResponse;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final MessageResolver messageResolver;
    private final UserAuthenticationUseCase userAuthenticationUseCase;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistUseCase tokenBlacklistUseCase;
    private final ObjectMapper objectMapper;
    private final SecurityProperties securityProperties;

    public JwtAuthorizationFilter(
            MessageResolver messageResolver,
            UserAuthenticationUseCase userAuthenticationUseCase,
            ObjectMapper objectMapper,
            SecurityProperties securityProperties) {
        this(
                messageResolver,
                userAuthenticationUseCase,
                null,
                null,
                objectMapper,
                securityProperties);
    }

    @Autowired
    public JwtAuthorizationFilter(
            MessageResolver messageResolver,
            UserAuthenticationUseCase userAuthenticationUseCase,
            @Autowired(required = false) JwtProvider jwtProvider,
            @Autowired(required = false) TokenBlacklistUseCase tokenBlacklistUseCase,
            ObjectMapper objectMapper,
            SecurityProperties securityProperties) {
        this.messageResolver = messageResolver;
        this.userAuthenticationUseCase = userAuthenticationUseCase;
        this.jwtProvider = jwtProvider;
        this.tokenBlacklistUseCase = tokenBlacklistUseCase;
        this.objectMapper = objectMapper;
        this.securityProperties = securityProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // If Bearer token or API key credentials are provided, always execute filter to establish
        // security context
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return false;
        }
        if (isApiKeyRequest(request)) {
            return false;
        }

        String path = request.getRequestURI();

        return securityProperties.getWhitelistRoutes().stream()
                .anyMatch(pattern -> new AntPathMatcher().match(pattern, path));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Handle API Key or Client Credentials authentication
            if (isApiKeyRequest(request)) {
                try {
                    final String apiKey = request.getHeader("X-API-KEY");
                    final String clientId = request.getHeader("Client-Id");
                    final String clientKey = request.getHeader("Client-Key");
                    final String traceId = request.getHeader("Trace-Id");

                    AuthenticatedUser user;
                    if (apiKey != null && !apiKey.isBlank()) {
                        user = userAuthenticationUseCase.getPrincipalByApiKey(apiKey.trim());
                    } else {
                        user =
                                userAuthenticationUseCase.getPrincipalByClientCredentials(
                                        clientId, clientKey);
                    }

                    if (user == null) {
                        setErrorResponse(null, request.getRequestURI(), response);
                        return;
                    }

                    MDC.put("traceId", traceId == null ? UUID.randomUUID().toString() : traceId);
                    MDC.put("userId", user.getUserId());

                    if (shouldAuthenticate(user.getUsername())) {
                        setSecurityAuthentication(user, request);
                    }
                } catch (Exception e) {
                    log.error(
                            "Exception during extract API key authentication: {}",
                            e.getMessage(),
                            e);
                    setErrorResponse(e.getMessage(), request.getRequestURI(), response);
                    return;
                }

                filterChain.doFilter(request, response);
                return;
            }

            // 2. Handle JWT Bearer token authentication
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                final String currentToken = extractTokenFromHeader(authHeader);

                if (jwtProvider != null && !jwtProvider.validateToken(currentToken)) {
                    setErrorResponse("Invalid or expired token", request.getRequestURI(), response);
                    return;
                }

                if (jwtProvider != null && tokenBlacklistUseCase != null) {
                    String jti = jwtProvider.extractJti(currentToken);
                    if (tokenBlacklistUseCase.isBlacklisted(jti)) {
                        setErrorResponse(
                                "Token has been revoked", request.getRequestURI(), response);
                        return;
                    }
                }

                var user = userAuthenticationUseCase.getPrincipal(currentToken);
                if (shouldAuthenticate(user.getUsername())) {
                    MDC.put("traceId", UUID.randomUUID().toString());
                    MDC.put("userId", user.getUserId());

                    setSecurityAuthentication(user, request);
                }
            } catch (Exception e) {
                log.error("Exception during token processing: {}", e.getMessage(), e);
                setErrorResponse(e.getMessage(), request.getRequestURI(), response);
                return;
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
            MDC.remove("userId");
            SecurityContextHolder.clearContext();
            com.linhtn.security_core.context.SecurityContextHolder.clearContext();
        }
    }

    private void setSecurityAuthentication(AuthenticatedUser user, HttpServletRequest request) {
        Collection<GrantedAuthority> authorities =
                Stream.of(
                                user.getRoles(),
                                user.getGroups(),
                                user.getResources(),
                                user.getPermissions())
                        .filter(Objects::nonNull)
                        .flatMap(Set::stream)
                        .map(SimpleGrantedAuthority::new)
                        .distinct()
                        .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Also ambiently populate platform security context
        com.linhtn.security_core.context.SecurityContextHolder.setContext(
                com.linhtn.security_core.context.SecurityContext.of(user));
    }

    private String extractTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }

    private boolean isApiKeyRequest(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey != null && !apiKey.isBlank()) {
            return true;
        }
        String clientId = request.getHeader("Client-Id");
        String clientKey = request.getHeader("Client-Key");
        return clientId != null && clientKey != null;
    }

    private boolean shouldAuthenticate(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void setErrorResponse(String metadata, String path, HttpServletResponse response)
            throws IOException {
        ApiErrorResponse apiError = new ApiErrorResponse();
        apiError.setErrorCode(HttpStatus.UNAUTHORIZED.name());
        apiError.setErrorMessage(messageResolver.getMessage(AuthError.UNAUTHORIZED.getMessage()));
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setErrorDetails(metadata);
        apiError.setPath(path);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
