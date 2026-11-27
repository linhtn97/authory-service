package com.linhtn.authory_service.config.security.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.config.security.provider.JwtProvider;
import com.linhtn.authory_service.modules.auth.application.port.in.PartnerAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.TokenBlacklistUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.sharedkernel.representation.ApiErrorResponse;
import com.linhtn.web.i18n.MessageResolver;

import tools.jackson.databind.ObjectMapper;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final MessageResolver messageResolver;
    private final PartnerAuthenticationUseCase partnerAuthenticationUseCase;
    private final UserAuthenticationUseCase userAuthenticationUseCase;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistUseCase tokenBlacklistUseCase;
    private final ObjectMapper objectMapper;
    private final SecurityProperties securityProperties;

    public JwtAuthorizationFilter(
            MessageResolver messageResolver,
            PartnerAuthenticationUseCase partnerAuthenticationUseCase,
            UserAuthenticationUseCase userAuthenticationUseCase,
            ObjectMapper objectMapper,
            SecurityProperties securityProperties) {
        this(
                messageResolver,
                partnerAuthenticationUseCase,
                userAuthenticationUseCase,
                null,
                null,
                objectMapper,
                securityProperties);
    }

    @Autowired
    public JwtAuthorizationFilter(
            MessageResolver messageResolver,
            PartnerAuthenticationUseCase partnerAuthenticationUseCase,
            UserAuthenticationUseCase userAuthenticationUseCase,
            @Autowired(required = false) JwtProvider jwtProvider,
            @Autowired(required = false) TokenBlacklistUseCase tokenBlacklistUseCase,
            ObjectMapper objectMapper,
            SecurityProperties securityProperties) {
        this.messageResolver = messageResolver;
        this.partnerAuthenticationUseCase = partnerAuthenticationUseCase;
        this.userAuthenticationUseCase = userAuthenticationUseCase;
        this.jwtProvider = jwtProvider;
        this.tokenBlacklistUseCase = tokenBlacklistUseCase;
        this.objectMapper = objectMapper;
        this.securityProperties = securityProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return securityProperties.getWhitelistRoutes().stream()
                .anyMatch(pattern -> new AntPathMatcher().match(pattern, path));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isPartnerRequest(request)) {
                try {
                    final String clientId = request.getHeader("Client-Id");
                    final String clientKey = request.getHeader("Client-Key");
                    final String traceId = request.getHeader("Trace-Id");

                    var principal = partnerAuthenticationUseCase.getPrincipal(clientId, clientKey);

                    if (principal == null) {
                        setErrorResponse(null, request.getRequestURI(), response);
                        return;
                    }

                    MDC.put("traceId", traceId == null ? UUID.randomUUID().toString() : traceId);
                    MDC.put("partnerId", String.valueOf(principal.id()));

                    if (shouldAuthenticate(principal.username())) {
                        setSecurityAuthentication(principal, request);
                    }
                } catch (Exception e) {
                    log.error(
                            "Exception during extract partner authentication: {}",
                            e.getMessage(),
                            e);
                    setErrorResponse(e.getMessage(), request.getRequestURI(), response);
                    return;
                }

                filterChain.doFilter(request, response);
                return;
            }

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

                var principal = userAuthenticationUseCase.getPrincipal(currentToken);
                if (shouldAuthenticate(principal.username())) {
                    MDC.put("traceId", UUID.randomUUID().toString());
                    MDC.put("userId", String.valueOf(principal.id()));

                    setSecurityAuthentication(principal, request);
                }
            } catch (Exception e) {
                log.error("Exception during token processing: {}", e.getMessage(), e);
                setErrorResponse(e.getMessage(), request.getRequestURI(), response);
                return;
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
            MDC.remove("partnerId");
            MDC.remove("userId");
            SecurityContextHolder.clearContext();
        }
    }

    private void setSecurityAuthentication(Principal principal, HttpServletRequest request) {
        var principalAuthorities = principal.authorities();
        Collection<GrantedAuthority> authorities = java.util.Collections.emptyList();
        if (principalAuthorities != null) {
            authorities =
                    Stream.of(
                                    principalAuthorities.roles(),
                                    principalAuthorities.groups(),
                                    principalAuthorities.resources(),
                                    principalAuthorities.permissions())
                            .filter(java.util.Objects::nonNull)
                            .flatMap(Set::stream)
                            .map(SimpleGrantedAuthority::new)
                            .distinct()
                            .collect(Collectors.toList());
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private String extractTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }

    private boolean isPartnerRequest(HttpServletRequest request) {
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
        response.setContentType("application/json");

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
