package com.linhtn.authory_service.config.security.filter;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.linhtn.authory_service.config.security.config.SecurityProperties;
import com.linhtn.authory_service.config.security.exception.error.AuthError;
import com.linhtn.sharedkernel.representation.ApiErrorResponse;
import com.linhtn.web.i18n.MessageResolver;

import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private final SecurityProperties securityProperties;
    private final JwtAuthorizationFilter authorizationFilter;
    private final CorrelationIdFilter correlationIdFilter;
    private final MessageResolver messageResolver;
    private final ObjectMapper objectMapper;

    public WebSecurityConfig(
            SecurityProperties securityProperties,
            @Lazy JwtAuthorizationFilter authorizationFilter,
            CorrelationIdFilter correlationIdFilter,
            MessageResolver messageResolver,
            ObjectMapper objectMapper) {
        this.securityProperties = securityProperties;
        this.authorizationFilter = authorizationFilter;
        this.correlationIdFilter = correlationIdFilter;
        this.messageResolver = messageResolver;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                securityProperties
                                                        .getWhitelistRoutes()
                                                        .toArray(new String[0]))
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET,
                                                securityProperties
                                                        .getSwaggerRoutes()
                                                        .toArray(new String[0]))
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exceptionHandling ->
                                exceptionHandling
                                        .authenticationEntryPoint(
                                                (request, response, authException) ->
                                                        setErrorResponse(
                                                                HttpStatus.UNAUTHORIZED.value(),
                                                                HttpStatus.UNAUTHORIZED
                                                                        .getReasonPhrase(),
                                                                AuthError.UNAUTHORIZED.getMessage(),
                                                                authException.getMessage(),
                                                                request.getRequestURI(),
                                                                response))
                                        .accessDeniedHandler(
                                                (request, response, accessDeniedException) ->
                                                        setErrorResponse(
                                                                HttpStatus.FORBIDDEN.value(),
                                                                HttpStatus.FORBIDDEN
                                                                        .getReasonPhrase(),
                                                                AuthError.ACCESS_DENIED
                                                                        .getMessage(),
                                                                accessDeniedException.getMessage(),
                                                                request.getRequestURI(),
                                                                response)))
                .logout(
                        logout ->
                                logout.logoutUrl("/api/v1/auth/logout")
                                        // .addLogoutHandler(logoutHandler)
                                        .logoutSuccessHandler(
                                                (request, response, authentication) ->
                                                        SecurityContextHolder.clearContext()));

        return httpSecurity.build();
    }

    private void setErrorResponse(
            int status,
            String error,
            String message,
            String metadata,
            String path,
            HttpServletResponse response)
            throws IOException {
        ApiErrorResponse apiError = new ApiErrorResponse();
        apiError.setErrorCode(error);
        apiError.setErrorMessage(messageResolver.getMessage(message));
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setErrorDetails(metadata);
        apiError.setPath(path);

        response.setStatus(status);
        response.setContentType("application/json");

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
