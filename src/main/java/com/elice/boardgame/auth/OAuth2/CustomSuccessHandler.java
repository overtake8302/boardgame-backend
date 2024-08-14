//package com.elice.boardgame.auth.OAuth2;
//
//import com.elice.boardgame.auth.OAuth2.dto.CustomOAuth2User;
//import com.elice.boardgame.auth.jwt.JWTUtil;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import java.util.Collection;
//import java.io.IOException;
//import java.util.Iterator;
//
////로그인 성공시 쿠키 발급
//@Component
//public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JWTUtil jwtUtil;
//
//    public CustomSuccessHandler(JWTUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//
//        //OAuth2User
//        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
//
//        String username = customUserDetails.getUsername();
////
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();
//
//        String token = jwtUtil.createJwt(username, role, 60*60*60L);
//
//        // "Authorization"이라는 이름의 쿠키 생성
//        Cookie authCookie = createCookie("Authorization", token);
////        Cookie authCookie = createCookie("JWT", token);
//        authCookie.setHttpOnly(true); // XSS 공격을 방지하기 위해 HttpOnly 설정
//        authCookie.setPath("/");      // 쿠키의 유효 경로를 설정
//
//        response.addCookie(authCookie);
//        response.sendRedirect("http://localhost:3000/");
//    }
//
//    private Cookie createCookie(String key, String value) {
//
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(60*60*60);
//        //cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//
//        return cookie;
//    }
//}