package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.DTO.RatingCountDTO;
import com.elice.boardgame.category.service.RatingService;
import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game-rate")
public class UserGameRateController {

    private final RatingService userGameRateService;

    // 내가 평점을 준 게임들 평점별로 찾기
    @GetMapping("/ratings/counts/{userId}")
    public ResponseEntity<List<RatingCountDTO>> getRatingCounts(@PathVariable Long userId) {
        List<RatingCountDTO> ratingCounts = userGameRateService.getRatingCounts(userId);
        return ResponseEntity.ok(ratingCounts);
    }

    // 평점 n점을 준 게임 찾기
    @GetMapping("/ratings/games/{userId}")
    public ResponseEntity<List<BoardGame>> getGamesByRating(@RequestParam Double rate, @PathVariable Long userId) {
        List<BoardGame> games = userGameRateService.getGamesByRating(userId, rate);
        return ResponseEntity.ok(games);
    }
}
