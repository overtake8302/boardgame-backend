package com.elice.boardgame.category.controller;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.category.dto.RatingCountDto;
import com.elice.boardgame.category.service.RatingService;
import com.elice.boardgame.common.annotation.CurrentUser;
import com.elice.boardgame.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "평점으로 게임 조회", description = "사용자가 준 평점에 따라 게임을 조회합니다.", parameters = {
        @Parameter(name = "rate", description = "평점", required = true, schema = @Schema(type = "Double", example = "4.5")),
        @Parameter(name = "user", description = "현재 사용자", required = true, schema = @Schema(implementation = User.class))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음")
    })
    @GetMapping("/ratings/games")
    public ResponseEntity<CommonResponse<RatingCountDto>> getGamesByRating(@RequestParam Double rate, @CurrentUser User user) {
        RatingCountDto gamesByRating = userGameRateService.getGamesByRating(user.getId(), rate);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(3, TimeUnit.SECONDS))
            .body(CommonResponse.<RatingCountDto>builder()
                .payload(gamesByRating)
                .message("")
                .status(200)
                .build());
    }
}
