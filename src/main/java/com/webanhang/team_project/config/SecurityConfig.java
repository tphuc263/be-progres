package com.webanhang.team_project.config;

import com.webanhang.team_project.security.jwt.AuthTokenFilter;
import com.webanhang.team_project.security.jwt.JwtEntryPoint;
import com.webanhang.team_project.security.userdetails.AppUserDetailsService;
import com.webanhang.team_project.utils.ErrorResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    @Value("${api.prefix}")
    private String API;

    private final AppUserDetailsService userDetailsService;
    private final JwtEntryPoint authEntryPoint;
    private final AuthTokenFilter authTokenFilter;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final ErrorResponseUtils errorResponseUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                errorResponseUtils.sendAccessDeniedError(response,
                "You do not have permission to access this resource.");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        List<String> securedUrls = List.of(API + "/carts/**", API + "/cartItems/**", API + "/orders/**");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(API + "/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(API + "/seller/**").hasAnyAuthority("SELLER", "ADMIN")
                        .requestMatchers(API + "/customer/**").hasAnyAuthority("CUSTOMER", "ADMIN")
                        .requestMatchers(securedUrls.toArray(String[]::new)).authenticated()
                        .requestMatchers("/oauth2/**").permitAll()
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                        .successHandler((request, response, authentication) -> response
                                .sendRedirect("/api/v1/customer/dashboard"))
                        .failureHandler((request, response, exception) -> {
                            log.error("Authentication error: {}", exception.getMessage());
                            response.sendRedirect("/login?error=" + exception.getMessage());
                        }))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
