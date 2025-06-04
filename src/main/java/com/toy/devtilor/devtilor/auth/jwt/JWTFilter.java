package com.toy.devtilor.devtilor.auth.jwt;

import com.toy.devtilor.devtilor.domain.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // access token 가져오기
        String accessToken = request.getHeader("access");

        // access token 없으면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. access token 유효성 검사
            jwtUtil.isExpired(accessToken);
            authenticateFromAccessToken(accessToken);
            filterChain.doFilter(request, response);
            return;

        } catch (ExpiredJwtException e) {
            // Access Token 만료 시, Refresh Token 처리
            handleExpiredAccessToken(request, response, filterChain);
        }
    }

    // Access Token으로 인증 처리
    private void authenticateFromAccessToken(String accessToken) {
        String role = jwtUtil.getRole(accessToken);
        Long userId = jwtUtil.getUserId(accessToken);
        User user = User.builder().userId(userId).role(role).build();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    // Access Token 만료 시, Refresh Token 처리
    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Optional<String> optionalRefreshToken = extractRefreshTokenFromCookie(request);

        if (optionalRefreshToken.isEmpty()) {
            // Refresh Token 없으면 오류
            setErrorResponse(response, "access token expired, refresh token not found", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String refreshToken = optionalRefreshToken.get();

        Long userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // refresh 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            // refresh 토큰 만료되었을 때 로직 작성
            setErrorResponse(response, "refresh token expired", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // refresh 토큰이 DB에 있는지 확인
        boolean isValid = refreshTokenService.isValid(userId, refreshToken);
        if (!isValid) {
            setErrorResponse(response, "refresh token not valid (not in DB)", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 새로운 access &  refresh 토큰 발급
        String newAccess = jwtUtil.createJwt("access", userId, role, 600000L); // 10분
        String newRefresh = jwtUtil.createJwt("refresh", userId, role, 86400000L);

        // String newRefresh =  jwtUtil.
        refreshTokenService.updateRefreshToken(userId, refreshToken, newRefresh);

        // 응답 헤더에 새 토큰 설정
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        // 인증 객체 설정
        setAuthentication(userId, role);
        chain.doFilter(request, response);
    }


    private void setAuthentication(Long userId, String role) {
        User user = User.builder()
                .userId(userId)
                .role(role)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refresh"))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void setErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.print(message);
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
