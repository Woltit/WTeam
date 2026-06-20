package com.wteam.backend.security.rate_limit;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {
    private final RateLimitingService rateLimitingService;

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request,
                             @NonNull final HttpServletResponse response,
                             @NonNull final Object handler
    ) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        final String ip = getClientIp(request);
        final String path = request.getRequestURI();
        Bucket bucket;

        if (path.startsWith("/api/v1/auth/login")) {
            bucket = rateLimitingService.resolveLoginBucket(ip);
        } else if (path.startsWith("/api/v1/items") || path.startsWith("/api/v1/categories")) {
            bucket = rateLimitingService.resolvePublicApiBucket(ip);
        } else {
            return true;
        }

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
            return false;
        }
    }


    private String getClientIp(@NonNull final HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0].strip();
    }
}
