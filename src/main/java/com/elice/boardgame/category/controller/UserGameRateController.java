package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.RatingCountDto;
import com.elice.boardgame.category.service.RatingService;
import com.elice.boardgame.common.dto.CommonResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class UserGameRateController {

    private final RatingService userGameRateService;

    // 평점 n점을 준 게임 찾기
    @GetMapping("/ratings/games/{userId}")
    public ResponseEntity<CommonResponse<RatingCountDto>> getGamesByRating(@RequestParam Double rate, @PathVariable Long userId) {
        RatingCountDto gamesByRating = userGameRateService.getGamesByRating(userId, rate);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .body(CommonResponse.<RatingCountDto>builder()
                .payload(gamesByRating)
                .message("")
                .status(200)
                .build());
    }
}
