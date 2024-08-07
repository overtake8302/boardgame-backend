package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.RatingCountDto;
import com.elice.boardgame.category.service.RatingService;
import com.elice.boardgame.common.dto.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    // 내가 평점을 준 게임들 평점별로 찾기
    @GetMapping("/ratings/counts/{userId}")
    public CommonResponse<List<RatingCountDto>> getRatingCounts(@PathVariable Long userId) {
        List<RatingCountDto> ratingCounts = userGameRateService.getRatingCounts(userId);
        return CommonResponse.<List<RatingCountDto>>builder()
            .payload(ratingCounts)
            .message("Success")
            .status(200)
            .build();
    }

    // 평점 n점을 준 게임 찾기
    @GetMapping("/ratings/games/{userId}")
    public CommonResponse<RatingCountDto> getGamesByRating(@RequestParam Double rate, @PathVariable Long userId) {
        RatingCountDto gamesByRating = userGameRateService.getGamesByRating(userId, rate);
        return CommonResponse.<RatingCountDto>builder()
            .payload(gamesByRating)
            .message("Success")
            .status(200)
            .build();
    }
}
