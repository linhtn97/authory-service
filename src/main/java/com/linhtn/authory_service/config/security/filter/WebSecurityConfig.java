package com.linhtn.authory_service.config.security.filter;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.config.security.config.SecurityProperties;
import com.linhtn.authory_service.config.security.exception.error.AuthError;
import com.linhtn.shared_kernel.representation.api.ApiErrorResponse;
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
import org.springframework.security.web.util.matcher.RequestMatcher;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private final SecurityProperties securityProperties;
    private final JwtAuthorizationFilter authorizationFilter;
    private final I18nHelper i18nHelper;
    private final ObjectMapper objectMapper;

    public WebSecurityConfig(SecurityProperties securityProperties, @Lazy JwtAuthorizationFilter authorizationFilter, I18nHelper i18nHelper, ObjectMapper objectMapper) {
        this.securityProperties = securityProperties;
        this.authorizationFilter = authorizationFilter;
        this.i18nHelper = i18nHelper;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(securityProperties.getWhitelistRoutes().toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.GET, securityProperties.getSwaggerRoutes().toArray(new String[0])).permitAll()
                        .requestMatchers(isInternalRequest()).permitAll() // <- add this
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) ->
                                setErrorResponse(
                                        HttpStatus.UNAUTHORIZED.value(),
                                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                        AuthError.UNAUTHORIZED.getMessage(),
                                        authException.getMessage(),
                                        request.getRequestURI(),
                                        response
                                )
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                setErrorResponse(
                                        HttpStatus.FORBIDDEN.value(),
                                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                                        AuthError.ACCESS_DENIED.getMessage(),
                                        accessDeniedException.getMessage(),
                                        request.getRequestURI(),
                                        response
                                )
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        //.addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );

        return httpSecurity.build();
    }

    private void setErrorResponse(int status, String error, String message, String metadata, String path, HttpServletResponse response) throws IOException {
        ApiErrorResponse apiError = new ApiErrorResponse();
        apiError.setErrorCode(error);
        apiError.setErrorMessage(i18nHelper.getMessage(message));
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setErrorDetails(metadata);
        apiError.setPath(path);

        response.setStatus(status);
        response.setContentType("application/json");

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(apiError)
        );
    }

    private RequestMatcher isInternalRequest() {
        return request -> {
            String internal = request.getHeader("X-Internal-Call");

            if ("true".equalsIgnoreCase(internal)) {
                return true; // Internal request by domain
            }

            return false; // Require authentication for external calls
        };
    }
}

