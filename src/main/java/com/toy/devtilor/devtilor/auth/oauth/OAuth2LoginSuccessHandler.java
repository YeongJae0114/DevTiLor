package com.toy.devtilor.devtilor.auth.oauth;

import com.toy.devtilor.devtilor.auth.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JwtProvider jwtProvider;

    private static final String FRONTEND_REDIRECT_URL = "http://localhost:3000/oauth-success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid authentication type");
            return;
        }

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        if (client == null) {
            log.error("OAuth2 Client not found for user: {}", oauthToken.getName());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 Client not found");
            return;
        }

        // ✅ Access Token from Google
        String googleAccessToken = client.getAccessToken().getTokenValue();
        log.info("🔑 Google Access Token: {}", googleAccessToken);

        // ✅ 사용자 정보 가져오기
        String email = oauthToken.getPrincipal().getAttribute("email");
        String role = oauthToken.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_USER");

        if (email == null || email.isBlank()) {
            log.warn("Email not found in OAuth2User attributes");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Email not found");
            return;
        }

        // ✅ JWT 발급
        String jwt = jwtProvider.createToken(email, role);

        // ✅ 응답 헤더 전달
        response.setHeader("Authorization", "Bearer " + jwt);
        response.setHeader("Google-Access-Token", googleAccessToken);

        // ✅ 리디렉션
        response.sendRedirect(FRONTEND_REDIRECT_URL);
    }
}
