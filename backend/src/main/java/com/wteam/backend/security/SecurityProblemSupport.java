package com.wteam.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

/**
 * Утиліта для відповідей Spring Security у форматі ProblemDetail (RFC 7807).
 */
public final class SecurityProblemSupport {

    private SecurityProblemSupport() {
    }

    public static void writeProblemDetail(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            HttpStatus status,
            String detail
    ) throws IOException {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setType(URI.create("https://httpstatuses.com/" + status.value()));

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problemDetail);
    }
}
