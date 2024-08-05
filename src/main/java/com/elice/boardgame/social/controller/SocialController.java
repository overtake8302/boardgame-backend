package com.elice.boardgame.social.controller;

import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.social.dto.SocialRequest;
import com.elice.boardgame.social.service.SocialService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
@Slf4j
public class SocialController {

    private final SocialService socialService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<Long>>> getFriends(@ModelAttribute PaginationRequest paginationRequest) {
        Long userId = 1L; // 로그인된 계정 아이디 받아오는 메서드로 변경
        int page = paginationRequest.getPage();
        int size = paginationRequest.getSize();
        List<Long> friends = socialService.getFriendIds(userId, page, size);
        CommonResponse<List<Long>> response = CommonResponse.<List<Long>>builder()
            .payload(friends)
            .message("Friends retrieved successfully")
            .status(200)
            .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> addFriend(@RequestBody SocialRequest socialRequest) {
        socialService.addFriend(socialRequest);
        CommonResponse<Void> response = CommonResponse.<Void>builder()
            .payload(null)
            .message("Friend added successfully.")
            .status(200)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        socialService.removeFriend(userId, friendId);
        CommonResponse<Void> response = CommonResponse.<Void>builder()
            .payload(null)
            .message("Friend removed successfully.")
            .status(200)
            .build();
        return ResponseEntity.ok(response);
    }
}

