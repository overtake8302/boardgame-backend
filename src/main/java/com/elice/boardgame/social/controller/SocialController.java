package com.elice.boardgame.social.controller;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.service.AuthService;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.social.service.SocialService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
@Slf4j
public class SocialController {

    private final SocialService socialService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<Long>>> getFriends(@ModelAttribute PaginationRequest paginationRequest) {
        User currentUser = authService.getCurrentUser();
        Long userId = currentUser.getId();
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

    @PostMapping("/{friendId}")
    public ResponseEntity<CommonResponse<Void>> addFriend(@PathVariable Long friendId) {
        User currentUser = authService.getCurrentUser();
        Long userId = currentUser.getId();
        socialService.addFriend(userId, friendId);
        CommonResponse<Void> response = CommonResponse.<Void>builder()
            .payload(null)
            .message("Friend added successfully.")
            .status(200)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<CommonResponse<Void>> removeFriend(@PathVariable Long friendId) {
        User currentUser = authService.getCurrentUser();
        Long userId = currentUser.getId();
        socialService.removeFriend(userId, friendId);
        CommonResponse<Void> response = CommonResponse.<Void>builder()
            .payload(null)
            .message("Friend removed successfully.")
            .status(200)
            .build();
        return ResponseEntity.ok(response);
    }
}

