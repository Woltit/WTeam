package com.wteam.backend.config.logging;

import com.wteam.backend.security.dto.UserPrincipalDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Фільтр для додавання MDC (Mapped Diagnostic Context) у логи.
 * <p>
 * Додає унікальний traceId для кожного запиту та userId (якщо користувач авторизований).
 * Це дозволяє легко фільтрувати логи в консолі або Kibana для конкретного користувача.
 * </p>
 */
@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";
    private static final String USER_ID = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Генеруємо унікальний ID для поточного запиту
            MDC.put(TRACE_ID, UUID.randomUUID().toString().substring(0, 8));

            // Спробуємо дістати ID користувача, якщо він вже авторизований
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipalDto principal) {
                MDC.put(USER_ID, String.valueOf(principal.id()));
            } else {
                MDC.put(USER_ID, "anonymous");
            }

            filterChain.doFilter(request, response);
        } finally {
            // Обов'язково очищаємо MDC після завершення запиту, щоб уникнути витоку даних між потоками
            MDC.remove(TRACE_ID);
            MDC.remove(USER_ID);
        }
    }
}
