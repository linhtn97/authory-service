package com.linhtn.authory_service.config.security.provider;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.linhtn.authory_service.config.security.config.SecurityProperties;
import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.config.security.model.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {
    private final String secretKey;
    private final RsaKeyManager rsaKeyManager;

    @Min(1)
    private long accessExpiration;

    @Min(1)
    private long refreshExpiration;

    public JwtProvider(SecurityProperties securityProperties) {
        this(securityProperties, null);
    }

    @Autowired
    public JwtProvider(
            SecurityProperties securityProperties,
            @Autowired(required = false) RsaKeyManager rsaKeyManager) {
        this.secretKey =
                securityProperties.getJwt() != null
                        ? securityProperties.getJwt().getSecretKey()
                        : null;
        this.accessExpiration =
                securityProperties.getJwt() != null
                        ? securityProperties.getJwt().getAccessExpiration()
                        : 3600000L;
        this.refreshExpiration =
                securityProperties.getJwt() != null
                        ? securityProperties.getJwt().getRefreshExpiration()
                        : 604800000L;
        this.rsaKeyManager = rsaKeyManager;
    }

    private SecretKey getSignKey() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("JWT secret key is not configured in authory-service");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(Principal principal, long expiration) {
        Instant now = Instant.now();
        var builder =
                Jwts.builder()
                        .subject(principal.id().toString())
                        .issuedAt(Date.from(now))
                        .expiration(Date.from(now.plusMillis(expiration)))
                        .claim("jti", UUID.randomUUID().toString())
                        .claim("uid", principal.id().toString())
                        .claim("preferred_username", principal.username())
                        .claim("username", principal.username())
                        .claim("email", principal.email());

        if (principal.authorities() != null
                && principal.authorities().roles() != null
                && !principal.authorities().roles().isEmpty()) {
            builder.claim("roles", principal.authorities().roles());
        }

        if (rsaKeyManager != null && rsaKeyManager.isRsaEnabled()) {
            return builder.header()
                    .keyId(RsaKeyManager.KEY_ID)
                    .and()
                    .signWith(rsaKeyManager.getPrivateKey())
                    .compact();
        } else {
            return builder.signWith(getSignKey()).compact();
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            var parserBuilder = Jwts.parser();
            if (rsaKeyManager != null && rsaKeyManager.isRsaEnabled()) {
                parserBuilder.verifyWith(rsaKeyManager.getPublicKey());
            } else {
                parserBuilder.verifyWith(getSignKey());
            }
            return parserBuilder.build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid or expired token", e);
        }
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(Principal principal, TokenType tokenType) {
        long expiration;
        if (tokenType.equals(TokenType.MFA_PENDING)) {
            expiration = 300000L; // 5 minutes
        } else if (tokenType.equals(TokenType.ACCESS)) {
            expiration = accessExpiration;
        } else {
            expiration = refreshExpiration;
        }
        return buildToken(principal, expiration);
    }

    public UUID extractUserId(String token) {
        String sub = extractClaims(token, Claims::getSubject);
        if (sub != null && !sub.isBlank()) {
            try {
                return UUID.fromString(sub);
            } catch (IllegalArgumentException ignored) {
            }
        }
        String uid = extractClaims(token, claims -> claims.get("uid", String.class));
        return uid != null ? UUID.fromString(uid) : null;
    }

    public String extractUsername(String token) {
        String preferredUsername =
                extractClaims(token, claims -> claims.get("preferred_username", String.class));
        if (preferredUsername != null && !preferredUsername.isBlank()) {
            return preferredUsername;
        }
        return extractClaims(token, claims -> claims.get("username", String.class));
    }

    public String extractJti(String token) {
        return extractClaims(token, claims -> claims.get("jti", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String extractEmail(String token) {
        return extractClaims(token, claims -> claims.get("email", String.class));
    }

    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        try {
            List<String> roles = extractClaims(token, claims -> claims.get("roles", List.class));
            return roles != null ? new HashSet<>(roles) : Collections.emptySet();
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername != null
                    && extractedUsername.equals(username)
                    && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}
