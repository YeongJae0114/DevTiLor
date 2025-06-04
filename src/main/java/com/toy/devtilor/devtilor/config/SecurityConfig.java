package com.toy.devtilor.devtilor.config;

import com.toy.devtilor.devtilor.auth.jwt.JWTFilter;
import com.toy.devtilor.devtilor.auth.jwt.JWTUtil;
import com.toy.devtilor.devtilor.auth.jwt.RefreshTokenService;
import com.toy.devtilor.devtilor.auth.oauth.OAuth2LoginFailureHandler;
import com.toy.devtilor.devtilor.auth.oauth.OAuth2LoginSuccessHandler;
import com.toy.devtilor.devtilor.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    private static final String[] PUBLIC_STATIC_RESOURCES = {
            "/", "/css/**", "/images/**", "/js/**", "/favicon.ico" , "/actuator/prometheus/**"
    };

    private static final String[] PUBLIC_API_ENDPOINTS = {
           "/api/public/**", "/oauth2/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler)
        );

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers(PUBLIC_STATIC_RESOURCES).permitAll()
                .requestMatchers(PUBLIC_API_ENDPOINTS).permitAll()
                .anyRequest().authenticated());

        http.addFilterBefore(new JWTFilter(jwtUtil, refreshTokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
