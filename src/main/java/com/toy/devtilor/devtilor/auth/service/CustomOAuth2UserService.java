package com.toy.devtilor.devtilor.auth.service;

import com.toy.devtilor.devtilor.auth.oauth.CustomOAuth2User;
import com.toy.devtilor.devtilor.auth.oauth.GoogleResponse;
import com.toy.devtilor.devtilor.auth.oauth.OAuth2Response;
import com.toy.devtilor.devtilor.domain.user.entity.User;
import com.toy.devtilor.devtilor.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        OAuth2Response oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        User existData = userRepository.findByUsername(username);

        String role = "ROLE_USER";
        if (existData == null) {
            User newUser = User.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .role(role)
                    .build();

            userRepository.save(newUser);
        }
        else {
            existData = User.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .role(role)
                    .build();
            // 최신 이메일 정보 동기화
            userRepository.save(existData);
        }
        return new CustomOAuth2User(oAuth2Response, role);
    }
}
