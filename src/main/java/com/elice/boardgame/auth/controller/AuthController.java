package com.elice.boardgame.auth.controller;

import com.elice.boardgame.auth.dto.CustomUserDetails;
import com.elice.boardgame.auth.dto.JoinDTO;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.auth.service.JoinService;
import com.elice.boardgame.common.annotation.CurrentUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    private final JoinService joinService;
    private final UserRepository userRepository;

    public AuthController(JoinService joinService, UserRepository userRepository) {
        this.joinService = joinService;
        this.userRepository = userRepository;
    }

    @PostMapping("/join")
    public String joinProcess(@RequestBody JoinDTO joinDTO) {
        joinService.joinProcess(joinDTO);
        return "register ok";
    }

    @GetMapping("/test/me")
    public String getCurrentUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails != null) {
            User user = userRepository.findByUsername(customUserDetails.getUsername());
            if (user != null) {
                return "User: " + user.getUsername() + ", Role: "
                    + customUserDetails.getAuthorities().toString() + ", Age: " + user.getAge();
            }
        }
        return "User not authenticated";
    }

//    @PostMapping("/logout")
//    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        // JWT 쿠키 삭제
//        Cookie cookie = new Cookie("JWT", null);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(0);
//
//        response.addCookie(cookie);
//    }
    @PostMapping("/user/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // "JWT" 쿠키 삭제
        Cookie jwtCookie = new Cookie("JWT", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // 쿠키 만료 시각을 0으로 설정하여 즉시 삭제

        // "Authorization" 쿠키 삭제 (소셜 로그인 시 생성된 쿠키)
        Cookie authCookie = new Cookie("Authorization", null);
        authCookie.setHttpOnly(true);
        authCookie.setPath("/");
        authCookie.setMaxAge(0);

        // 두 쿠키를 모두 응답에 추가하여 삭제
        response.addCookie(jwtCookie);
        response.addCookie(authCookie);
    }

//    @GetMapping("/login-check")
//    public ResponseEntity<Boolean> loginCheck(@CurrentUser User user) {
//
//        if (user == null) {
//            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
//        } else if (user != null && user.getId() != null) {
//            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
//        }
//
//        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
//    }

    @GetMapping("/login-check")
    public ResponseEntity<Boolean> loginCheck(@CurrentUser User user) {
        if (user == null || user.getId() == null) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }


    @GetMapping("/admin-check")
    public ResponseEntity<Boolean> adminCheck(@CurrentUser User user) {
        if (user == null) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
        } else if (user != null && user.getId() != null) {
            if (user.getRole().equals("ROLE_ADMIN")) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else if (user.getRole().equals("ROLE_USER")) {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
    }


}
