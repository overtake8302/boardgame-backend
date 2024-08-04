package com.elice.boardgame.social.controller;

import com.elice.boardgame.social.dto.SocialRequest;
import com.elice.boardgame.social.service.SocialService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<Long>> getFriends(@RequestParam int page, @RequestParam int size) {
        Long userId = 1L; // 로그인된 계정 아이디 받아오는 메서드로 변경
        List<Long> friends = socialService.getFriendIds(userId, page, size);
        return ResponseEntity.ok(friends);
    }

    @PostMapping
    public ResponseEntity<String> addFriend(@RequestBody SocialRequest socialRequest) {
        socialService.addFriend(socialRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Friend added successfully.");
    }

    @DeleteMapping
    public ResponseEntity<String> removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        socialService.removeFriend(userId, friendId);
        return ResponseEntity.ok("Friend removed successfully.");
    }
}

