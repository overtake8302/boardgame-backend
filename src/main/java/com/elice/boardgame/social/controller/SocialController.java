package com.elice.boardgame.social.controller;
//
//import com.elice.boardgame.auth.entity.User;
//import com.elice.boardgame.auth.service.AuthService;
//import com.elice.boardgame.common.dto.CommonResponse;
//import com.elice.boardgame.common.dto.PaginationRequest;
//import com.elice.boardgame.social.dto.SocialResponse;
//import com.elice.boardgame.social.service.SocialService;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/social")
//@Slf4j
//public class SocialController {
//
//    private final SocialService socialService;
//    private final AuthService authService;
//
//    @GetMapping
//    public ResponseEntity<CommonResponse<Page<SocialResponse>>> getFriends(@ModelAttribute PaginationRequest paginationRequest) {
//        User currentUser = authService.getCurrentUser();
//        Long userId = currentUser.getId();
//        Pageable pageable = PageRequest.of(paginationRequest.getPage(), paginationRequest.getSize());
//        Page<SocialResponse> friends = socialService.getFriendIds(userId, pageable);
//        CommonResponse<Page<SocialResponse>> response = CommonResponse.<Page<SocialResponse>>builder()
//            .payload(friends)
//            .message("Friends retrieved successfully")
//            .status(200)
//            .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/{friendId}")
//    public ResponseEntity<CommonResponse<Void>> addFriend(@PathVariable Long friendId) {
//        User currentUser = authService.getCurrentUser();
//        Long userId = currentUser.getId();
//        socialService.addFriend(userId, friendId);
//        CommonResponse<Void> response = CommonResponse.<Void>builder()
//            .payload(null)
//            .message("Friend added successfully.")
//            .status(200)
//            .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @DeleteMapping("/{friendId}")
//    public ResponseEntity<CommonResponse<Void>> removeFriend(@PathVariable Long friendId) {
//        User currentUser = authService.getCurrentUser();
//        Long userId = currentUser.getId();
//        socialService.removeFriend(userId, friendId);
//        CommonResponse<Void> response = CommonResponse.<Void>builder()
//            .payload(null)
//            .message("Friend removed successfully.")
//            .status(200)
//            .build();
//        return ResponseEntity.ok(response);
//    }
//}
//

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.service.AuthService;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.social.dto.SocialResponse;
import com.elice.boardgame.social.service.SocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
@Slf4j
public class SocialController {

    private final SocialService socialService;
    private final AuthService authService;

    @GetMapping
    @Operation(summary = "친구 목록 조회", description = "페이지네이션을 통해 사용자의 친구 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록이 성공적으로 조회되었습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근입니다.")
    })
    public ResponseEntity<CommonResponse<Page<SocialResponse>>> getFriends(
            @RequestParam int page, @RequestParam int size) {
        User currentUser = authService.getCurrentUser();
        Long userId = currentUser.getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<SocialResponse> friends = socialService.getFriendIds(userId, pageable);
        CommonResponse<Page<SocialResponse>> response = CommonResponse.<Page<SocialResponse>>builder()
                .payload(friends)
                .message("친구 목록이 성공적으로 조회되었습니다.")
                .status(200)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{friendId}")
    @Operation(summary = "친구 추가", description = "주어진 ID를 통해 새로운 친구를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "친구가 성공적으로 추가되었습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근입니다.")
    })
    public ResponseEntity<CommonResponse<Void>> addFriend(
            @PathVariable Long friendId) {
        User currentUser = authService.getCurrentUser();
        Long userId = currentUser.getId();
        socialService.addFriend(userId, friendId);
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .payload(null)
                .message("친구가 성공적으로 추가되었습니다.")
                .status(200)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{friendId}")
    @Operation(summary = "친구 제거", description = "주어진 ID를 통해 친구를 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구가 성공적으로 제거되었습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근입니다.")
    })
    public ResponseEntity<CommonResponse<Void>> removeFriend(
            @PathVariable Long friendId) {
        User currentUser = authService.getCurrentUser();
        Long userId = currentUser.getId();
        socialService.removeFriend(userId, friendId);
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .payload(null)
                .message("친구가 성공적으로 제거되었습니다.")
                .status(200)
                .build();
        return ResponseEntity.ok(response);
    }
}