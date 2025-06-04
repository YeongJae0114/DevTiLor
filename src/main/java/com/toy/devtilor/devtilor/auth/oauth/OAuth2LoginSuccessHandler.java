package com.toy.devtilor.devtilor.auth.oauth;

import com.toy.devtilor.devtilor.auth.jwt.JWTUtil;
import com.toy.devtilor.devtilor.auth.jwt.RefreshTokenService;
import com.toy.devtilor.devtilor.domain.user.entity.User;
import com.toy.devtilor.devtilor.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User.UserBuilder;
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
    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final OAuthTokenService oAuthTokenService;

    private static final String FRONTEND_REDIRECT_URL = "http://localhost:8080/oauth-success";

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

        // ✅ 사용자 정보 가져오기
        String email = oauthToken.getPrincipal().getAttribute("email");
        String name = oauthToken.getPrincipal().getAttribute("name");
        String picture = oauthToken.getPrincipal().getAttribute("picture");
        String role = oauthToken.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_USER");

        if (email == null || email.isBlank()) {
            log.warn("Email not found in OAuth2User attributes");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Email not found");
            return;
        }

        // user 가 존재하는 확인
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        if (existingUser.isEmpty()) {
            // 생성된 유저가 없다면 User 객체 저장
            user = User.builder()
                    .email(email)
                    .username(name)
                    .picture(picture)
                    .role(role)
                    .build();
            userRepository.save(user);

        }else {
            user = existingUser.get();
        }

        // JWT 발급
        String accessToken = jwtUtil.createJwt("access", user.getUserId(), user.getRole(), 600_000L);      // 10분
        String refreshToken = jwtUtil.createJwt("refresh", user.getUserId(), user.getRole(), 86_400_000L); // 1일

        // Access Token from Google 과 refreshToken 저장
        refreshTokenService.saveToken(user.getUserId(), refreshToken, 86400000L);
        oAuthTokenService.saveToken(user.getUserId(), googleAccessToken, 86400000L);

        //response.setHeader("Google-Access-Token", googleAccessToken);

        // 토큰 전달
        response.setHeader("access", accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        response.sendRedirect(FRONTEND_REDIRECT_URL);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
