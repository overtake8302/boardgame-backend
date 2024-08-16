package com.elice.boardgame.auth.controller;

import com.elice.boardgame.auth.dto.CustomUserDetails;
import com.elice.boardgame.auth.dto.JoinDTO;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.auth.service.AuthService;
import com.elice.boardgame.auth.service.JoinService;
import com.elice.boardgame.common.annotation.CurrentUser;
import com.elice.boardgame.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "사용자 인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JoinService joinService;

    public AuthController(AuthService authService, UserRepository userRepository, JoinService joinService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.joinService = joinService;
    }

    @Operation(
            summary = "회원 가입",
            description = "사용자 회원 가입을 처리합니다.",
            parameters = {
                    @Parameter(name = "username", description = "아이디", required = true, schema = @Schema(type = "string", example = "id1234")),
                    @Parameter(name = "password", description = "비밀번호", required = true, schema = @Schema(type = "string", example = "password123")),
                    @Parameter(name = "age", description = "사용자 나이", required = false, schema = @Schema(type = "integer", example = "25")),
                    @Parameter(name = "phonenumber", description = "사용자 전화번호", required = false, schema = @Schema(type = "string", example = "010-1234-5678")),
                    @Parameter(name = "name", description = "사용자 이름", required = false, schema = @Schema(type = "string", example = "elice")),
                    @Parameter(name = "profileImage", description = "프로필 이미지", required = false, schema = @Schema(type = "string", format = "binary"))
            }
    )
    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(
            @Parameter(description = "사용자 이름", required = true) @RequestParam("username") String username,
            @Parameter(description = "사용자 비밀번호", required = true) @RequestParam("password") String password,
            @Parameter(description = "사용자 나이", required = false) @RequestParam(value = "age", required = false) Integer age,
            @Parameter(description = "사용자 전화번호", required = false) @RequestParam(value = "phonenumber", required = false) String phonenumber,
            @Parameter(description = "사용자 이름", required = false) @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "프로필 이미지", required = false) @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setUsername(username);
        joinDTO.setPassword(password);
        joinDTO.setAge(age);
        joinDTO.setPhonenumber(phonenumber);
        joinDTO.setName(name);
        joinDTO.setProfileImage(profileImage);

        joinService.joinProcess(joinDTO);
        return ResponseEntity.ok("register ok");
    }

    @Operation(summary = "현재 사용자 정보 조회", description = "현재 인증된 사용자 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "현재 사용자 정보 반환"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
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

    @Operation(summary = "로그아웃", description = "사용자를 로그아웃 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping("/user/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(response);
    }

    @Operation(summary = "로그인 상태 확인", description = "현재 사용자가 로그인 상태인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 상태"),
            @ApiResponse(responseCode = "401", description = "로그인되지 않음", content = @Content)
    })
    @GetMapping("/login-check")
    public ResponseEntity<Boolean> loginCheck(@CurrentUser User user) {
        if (user == null || user.getId() == null) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @Operation(summary = "관리자 권한 확인", description = "현재 사용자가 관리자 권한을 가지고 있는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 권한 있음"),
            @ApiResponse(responseCode = "401", description = "관리자 권한 없음 또는 인증되지 않음", content = @Content)
    })
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

    @Operation(summary = "회원 탈퇴", description = "사용자의 계정을 탈퇴 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
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

