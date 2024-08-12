//package com.elice.boardgame.auth.jwt;
//
//import com.elice.boardgame.auth.dto.CustomUserDetails;
//import com.elice.boardgame.auth.entity.User;
//import com.elice.boardgame.auth.repository.UserRepository;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//public class JWTFilter extends OncePerRequestFilter {
//
//    private final JWTUtil jwtUtil;
//    private final UserRepository userRepository; // Change to final
//
//    public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository) {
//        this.jwtUtil = jwtUtil;
//        this.userRepository = userRepository; // Initialize UserRepository
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        // 쿠키에서 JWT 토큰을 찾음
//        String token = null;
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("JWT".equals(cookie.getName())) {
//                    token = cookie.getValue();
//                    break;
//                }
//            }
//        }
//
//        // 토큰이 없거나 유효하지 않으면 필터 체인 계속 진행
//        if (token == null || jwtUtil.isExpired(token)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // 토큰에서 username과 role 획득
//        String username = jwtUtil.getUsername(token);
//        String role = jwtUtil.getRole(token);
//
//        User user = userRepository.findByUsername(username);
//        if (user == null) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        CustomUserDetails customUserDetails = new CustomUserDetails(user);
//
//        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        filterChain.doFilter(request, response);
//    }
//}

package com.elice.boardgame.auth.jwt;

import com.elice.boardgame.auth.dto.CustomUserDetails;
import com.elice.boardgame.auth.OAuth2.dto.CustomOAuth2User;
import com.elice.boardgame.auth.OAuth2.dto.UserDTO;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository; // Optional, use if you need user details

    public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository; // Initialize UserRepository
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 쿠키에서 JWT 토큰을 찾음
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) { // 쿠키 이름을 "Authorization"으로 통일
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Authorization 헤더 검증 (쿠키에서 토큰을 찾지 못했을 경우 헤더에서 찾을 수 있도록 설정 가능)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 만료되었는지 확인
        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        if (userRepository != null) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                CustomUserDetails customUserDetails = new CustomUserDetails(user);
                Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } else {
            // UserRepository를 사용하지 않는 경우
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setRole(role);
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
