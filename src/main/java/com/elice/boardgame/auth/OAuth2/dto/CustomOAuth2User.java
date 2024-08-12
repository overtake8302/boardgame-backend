package com.elice.boardgame.auth.OAuth2.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//CustomOAuth2Service에서 OAuth2LoginAuthenticationProvider로 전달해 로그인하기 위한 dto
public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    public CustomOAuth2User(UserDTO userDTO) {

        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

//    @Override
//    public Map<String, Object> getAttributes() {
//        // 사용자 속성 반환
//        return userDTO != null ? Map.of(
//                "username", userDTO.getUsername(),
//                "name", userDTO.getName(),
//                "role", userDTO.getRole()
//        ) : Map.of();
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userDTO.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return userDTO.getName();
    }

    public String getUsername() {

        return userDTO.getUsername();
    }
}
