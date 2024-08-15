package com.elice.boardgame.auth.service;

import com.elice.boardgame.auth.dto.UpdateUserDTO;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.common.exceptions.UserErrorMessages;
import com.elice.boardgame.common.exceptions.UserException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final UserRepository userRepository;
//    private final UserService userService;
//
//    public User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserName = authentication.getName();
//        return userRepository.findByUsername(currentUserName);
//    }
//
//}

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;

    // 현재 사용자를 가져오는 메서드
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        return userRepository.findByUsername(currentUserName);
    }

    // 로그아웃 처리 메서드
    public void logout(HttpServletResponse response) {
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

    // 유저 탈퇴 후 로그아웃을 포함한 메서드
    public void withdrawUser(User user, HttpServletResponse response) {
        if (user == null) {
            throw new UserException(UserErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        // User의 상태를 'withdraw'로 변경
        user.setUserState("withdraw");

        // User 정보 업데이트
        userRepository.save(user);

        // 로그아웃 처리
        logout(response);
    }
}