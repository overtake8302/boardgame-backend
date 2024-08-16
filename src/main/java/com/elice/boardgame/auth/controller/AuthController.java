package com.elice.boardgame.auth.controller;

import com.elice.boardgame.auth.dto.CustomUserDetails;
import com.elice.boardgame.auth.dto.JoinDTO;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.auth.service.AuthService;
import com.elice.boardgame.auth.service.JoinService;
import com.elice.boardgame.common.annotation.CurrentUser;
import com.elice.boardgame.common.dto.CommonResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JoinService joinService;

    public AuthController(AuthService authService, UserRepository userRepository, JoinService joinService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam(value = "phonenumber", required = false) String phonenumber,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setUsername(username);
        joinDTO.setPassword(password);
        joinDTO.setAge(age);
        joinDTO.setPhonenumber(phonenumber);
        joinDTO.setName(name);
        joinDTO.setProfileImage(profileImage);

        System.out.println("cage : " + joinDTO.getAge());
        System.out.println("cphone : " + joinDTO.getPhonenumber());
        System.out.println("cname :" + joinDTO.getName());
        System.out.println("curl : " + joinDTO.getProfileImage());

        joinService.joinProcess(joinDTO);
        return ResponseEntity.ok("register ok");
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

    @PostMapping("/user/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(response);
    }

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

    @PutMapping("/withdraw")
    public ResponseEntity<CommonResponse<String>> withdrawUser(@CurrentUser User user, HttpServletResponse response) {
        try {
            authService.withdrawUser(user, response);
            return new ResponseEntity<>(CommonResponse.<String>builder()
                    .payload("User successfully withdrawn and logged out")
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(CommonResponse.<String>builder()
                    .payload("Failed to withdraw user")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

