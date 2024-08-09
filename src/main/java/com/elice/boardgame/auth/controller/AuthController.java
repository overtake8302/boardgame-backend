package com.elice.boardgame.auth.controller;

import com.elice.boardgame.auth.dto.CustomUserDetails;
import com.elice.boardgame.auth.dto.JoinDTO;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.auth.service.JoinService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
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
                return "User: " + user.getUsername() + ", Role: " + customUserDetails.getAuthorities().toString() + ", Age: " + user.getAge();
            }
        }
        return "User not authenticated";
    }

//    @PostMapping("/test/logout")
//    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//            new SecurityContextLogoutHandler().logout(request, response, auth);
//        }
//    }



    //react navigating
}
