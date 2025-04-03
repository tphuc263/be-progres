package com.webanhang.team_project.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webanhang.team_project.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class ErrorResponseUtils {
    private final ObjectMapper objectMapper;

    public void sendAuthenticationError(HttpServletResponse response, String message) throws IOException {
        sendError(response, "Authentication Error", message, HttpServletResponse.SC_UNAUTHORIZED);
    }

    public void sendAccessDeniedError(HttpServletResponse response, String message) throws IOException {
        sendError(response, "Access Denied Error", message, HttpServletResponse.SC_FORBIDDEN);
    }

    private void sendError(HttpServletResponse response, String error, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(
                error,
                message,
                status
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

}
