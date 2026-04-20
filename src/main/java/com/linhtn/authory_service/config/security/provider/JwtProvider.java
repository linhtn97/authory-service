package com.linhtn.authory_service.config.security.provider;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

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
    @NotEmpty private String secretKey;

    @Min(1)
    private long accessExpiration;

    @Min(1)
    private long refreshExpiration;

    public JwtProvider(SecurityProperties securityProperties) {
        this.secretKey = securityProperties.getJwt().getSecretKey();
        this.accessExpiration = securityProperties.getJwt().getAccessExpiration();
        this.refreshExpiration = securityProperties.getJwt().getRefreshExpiration();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(Principal principal, long expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)))
                .claim("jti", UUID.randomUUID().toString())
                .claim("uid", principal.id())
                .claim("username", principal.username())
                .claim("email", principal.email())
                .signWith(getSignKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            // Handle various JWT exceptions (invalid, expired, etc.)
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
        return buildToken(
                principal,
                tokenType.equals(TokenType.ACCESS) ? accessExpiration : refreshExpiration);
    }

    public String extractUsername(String token) {
        return extractClaims(token, claims -> claims.get("username", String.class));
    }

    public String extractJti(String token) {
        return extractClaims(token, claims -> claims.get("jti", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
