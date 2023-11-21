package com.wanted.safewallet.domain.auth.handler;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.safewallet.global.dto.response.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthorizationFailureHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        log.error("Jwt Authorization Fail: {}", accessDeniedException.getMessage());
        sendResponse(response, accessDeniedException);
    }

    private void sendResponse(HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        setResponseHeader(response);
        response.getWriter().write(getResponseBody(accessDeniedException));
    }

    private void setResponseHeader(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    private String getResponseBody(AccessDeniedException accessDeniedException)
        throws JsonProcessingException {
        CommonResponse<Object> responseBody = new CommonResponse<>(FORBIDDEN.value(),
            FORBIDDEN.name(), accessDeniedException.getMessage(), null);
        return objectMapper.writeValueAsString(responseBody);
    }
}
