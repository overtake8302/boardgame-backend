package com.elice.boardgame.social.controller;

import com.elice.boardgame.social.dto.SocialRequest;
import com.elice.boardgame.social.exception.SocialAlreadyExistsException;
import com.elice.boardgame.social.exception.SocialNotFoundException;
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
@RequestMapping("/api/social")
@Slf4j
public class SocialController {

    private final SocialService socialService;

    @GetMapping
    public ResponseEntity<List<Long>> getFriends(@RequestParam int page, @RequestParam int size) {
        Long userId = 1l; // 로그인된 계정 아이디 받아오는 메서드로 변경
        try {
            List<Long> friends = socialService.getFriendIds(userId, page, size);
            log.info("Retrieved friend IDs for user {}: {}", userId, friends);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            log.error("Failed to retrieve friends: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping
    public ResponseEntity<String> addFriend(@RequestBody SocialRequest socialRequest) {
        try {
            socialService.addFriend(socialRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Friend added successfully.");
        } catch (SocialAlreadyExistsException e) {
            log.error("Failed to add friend: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        try {
            socialService.removeFriend(userId, friendId);
            return ResponseEntity.ok("Friend removed successfully.");
        } catch (SocialNotFoundException e) {
            log.error("Failed to remove friend: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


}
