package com.linhtn.authory_service.config.security.filter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long WINDOW_MS = 60_000L;

    private static class RequestBucket {
        private final AtomicLong windowStart;
        private final AtomicInteger count;

        RequestBucket(long now) {
            this.windowStart = new AtomicLong(now);
            this.count = new AtomicInteger(0);
        }

        boolean allowRequest(long now, int maxRequests, long windowMs) {
            while (true) {
                long start = windowStart.get();
                if (now - start > windowMs) {
                    if (windowStart.compareAndSet(start, now)) {
                        count.set(1);
                        return true;
                    }
                    continue;
                }
                int current = count.get();
                if (current >= maxRequests) {
                    return false;
                }
                if (count.compareAndSet(current, current + 1)) {
                    return true;
                }
            }
        }
    }

    private final Map<String, RequestBucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isRateLimitedPath(path)) {
            String clientIp = getClientIp(request);
            if (!isAllowed(clientIp)) {
                log.warn("Rate limit exceeded for client IP: {} on path: {}", clientIp, path);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setHeader("Retry-After", "60");
                response.getWriter()
                        .write(
                                "{\"status\": 429, \"error\": \"Too Many Requests\", \"message\":"
                                        + " \"Rate limit exceeded. Try again in 60 seconds.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimitedPath(String path) {
        return path != null
                && (path.endsWith("/api/v1/auth/login")
                        || path.endsWith("/api/v1/auth/register")
                        || path.endsWith("/api/v1/auth/mfa/verify")
                        || path.endsWith("/api/v1/auth/forgot-password")
                        || path.endsWith("/api/v1/auth/reset-password"));
    }

    private boolean isAllowed(String clientIp) {
        long now = Instant.now().toEpochMilli();
        RequestBucket bucket = buckets.computeIfAbsent(clientIp, k -> new RequestBucket(now));
        return bucket.allowRequest(now, MAX_REQUESTS_PER_MINUTE, WINDOW_MS);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
