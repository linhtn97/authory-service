package com.linhtn.authory_service.config.security.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.config.security.config.SecurityProperties;
import com.linhtn.authory_service.config.security.exception.error.AuthError;
import com.linhtn.authory_service.modules.auth.service.PartnerAuthenticationService;
import com.linhtn.authory_service.modules.auth.service.UserAuthenticationService;
import com.linhtn.shared_kernel.representation.api.ApiErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final I18nHelper i18nHelper;
    private final PartnerAuthenticationService partnerAuthenticationService;
    private final UserAuthenticationService userAuthenticationService;
    private final ObjectMapper objectMapper;
    private final SecurityProperties securityProperties;

    public JwtAuthorizationFilter(
            I18nHelper i18nHelper,
            PartnerAuthenticationService partnerAuthenticationService,
            UserAuthenticationService userAuthenticationService,
            ObjectMapper objectMapper,
            SecurityProperties securityProperties) {
        this.i18nHelper = i18nHelper;
        this.partnerAuthenticationService = partnerAuthenticationService;
        this.userAuthenticationService = userAuthenticationService;
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

                    var principal = partnerAuthenticationService.getPrincipal(clientId, clientKey);

                    if (principal == null) {
                        setErrorResponse(null, request.getRequestURI(), response);
                        return;
                    }

                    MDC.put("traceId", traceId == null ? UUID.randomUUID().toString() : traceId);
                    MDC.put(
                            "partnerId",
                            String.valueOf(principal.id())); // or authUser.getUsername()

                    if (shouldAuthenticate(principal.username())) {
                        var principalAuthorities = principal.authorities();

                        Collection<GrantedAuthority> authorities =
                                Stream.of(
                                                principalAuthorities.roles(),
                                                principalAuthorities.groups(),
                                                principalAuthorities.resources(),
                                                principalAuthorities.permissions())
                                        .flatMap(Set::stream)
                                        .map(SimpleGrantedAuthority::new)
                                        .distinct()
                                        .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        principal, null, authorities);
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
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
                var principal = userAuthenticationService.getPrincipal(currentToken);
                if (shouldAuthenticate(principal.username())) {
                    // this way also can check token revoked and make sure this is access token not
                    // refresh token

                    MDC.put("traceId", UUID.randomUUID().toString());
                    MDC.put("userId", String.valueOf(principal.id())); // or authUser.getUsername()

                    var principalAuthorities = principal.authorities();

                    Collection<GrantedAuthority> authorities =
                            Stream.of(
                                            principalAuthorities.roles(),
                                            principalAuthorities.groups(),
                                            principalAuthorities.resources(),
                                            principalAuthorities.permissions())
                                    .flatMap(Set::stream)
                                    .map(SimpleGrantedAuthority::new)
                                    .distinct()
                                    .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                log.error("Exception during token processing: {}", e.getMessage(), e);
                setErrorResponse(e.getMessage(), request.getRequestURI(), response);
                return;
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private String extractTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }

    private boolean isPartnerRequest(HttpServletRequest request) {
        String partner = request.getHeader("X-Partner-Call");

        return "true".equalsIgnoreCase(partner);
    }

    private boolean shouldAuthenticate(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void setErrorResponse(String metadata, String path, HttpServletResponse response)
            throws IOException {
        ApiErrorResponse apiError = new ApiErrorResponse();
        apiError.setErrorCode(HttpStatus.UNAUTHORIZED.name());
        apiError.setErrorMessage(i18nHelper.getMessage(AuthError.UNAUTHORIZED.getMessage()));
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setErrorDetails(metadata);
        apiError.setPath(path);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
