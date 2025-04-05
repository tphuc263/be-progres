package com.webanhang.team_project.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.oauth2.failureRedirectUri}")
    private String defaultFailureRedirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.error("OAuth2 authentication failed: {}", exception.getMessage());

        // Lấy thông báo lỗi
        String errorMessage = exception.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Đăng nhập thất bại, vui lòng thử lại.";
        }

        // Mã hóa thông báo lỗi để an toàn khi đưa vào URL
        String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // Xây dựng URL với tham số lỗi
        String redirectUrl = UriComponentsBuilder.fromUriString(defaultFailureRedirectUri)
                .queryParam("error", encodedErrorMessage)
                .build().toUriString();

        log.info("Redirecting to: {}", redirectUrl);

        // Chuyển hướng người dùng đến trang đăng nhập với thông báo lỗi
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
