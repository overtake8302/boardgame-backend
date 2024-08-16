package com.elice.boardgame.category.controller;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.dto.RecentlyViewGameDto;
import com.elice.boardgame.category.service.LikeGenreService;
import com.elice.boardgame.common.annotation.CurrentUser;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.game.dto.GameResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class LikeGenreController {

    private final LikeGenreService likeGenreService;

    @Operation(summary = "유저 좋아요 처리", description = "유저가 특정 게임에 좋아요를 표시합니다.", parameters = {
        @Parameter(name = "gameId", description = "좋아요를 표시할 게임의 ID", required = true, schema = @Schema(type = "Long", example = "1"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 좋아요 처리됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음")
    })
    @PutMapping("/like/{gameId}")
    public Boolean handleLike(@CurrentUser User user, @PathVariable Long gameId) {
        return likeGenreService.likeGenreScore(user.getId(), gameId);
    }

    @Operation(summary = "유저 평점 처리", description = "유저가 특정 게임에 평점을 부여합니다.", parameters = {
        @Parameter(name = "gameId", description = "평점을 부여할 게임의 ID", required = true, schema = @Schema(type = "Long", example = "1")),
        @Parameter(name = "rating", description = "부여할 평점 값", required = true, schema = @Schema(type = "Double", example = "4.5"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 평점 처리됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음")
    })
    @PutMapping("/rate/{gameId}/{rating}")
    public Boolean handleRateScore(@CurrentUser User user, @PathVariable Long gameId, @PathVariable Double rating) {
        return likeGenreService.genreRatingScore(user.getId(), gameId, rating);
    }

    @Operation(summary = "유저별 좋아하는 장르 조회", description = "유저가 좋아하는 게임 장르 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 좋아하는 장르 목록 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음")
    })
    @GetMapping("/user/genre")
    public ResponseEntity<CommonResponse<List<GenreDto>>> getGenres(@CurrentUser User user) {
        List<GenreDto> genreDtos = likeGenreService.getGenres(user.getId());
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(3, TimeUnit.SECONDS))
            .body(CommonResponse.<List<GenreDto>>builder().payload(genreDtos).message("").status(200).build());
    }

    @Operation(summary = "추천 게임 조회", description = "유저의 좋아요 또는 평점에 기반하여 게임 목록을 조회합니다.", parameters = {
        @Parameter(name = "type", description = "조회할 게임 목록의 타입 (예: 'like','rate','genre/rate','genre')", required = true, schema = @Schema(type = "String", example = "like")),
        @Parameter(name = "page", description = "페이지 번호", required = true, schema = @Schema(type = "Integer", example = "0")),
        @Parameter(name = "size", description = "페이지당 항목 수", required = true, schema = @Schema(type = "Integer", example = "10"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 게임 목록 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음")
    })
    @GetMapping("/game")
    public ResponseEntity<CommonResponse<Page<GameResponseDto>>> getGames(@RequestParam String type, @CurrentUser User user, PaginationRequest paginationRequest) {
        Page<GameResponseDto> games = likeGenreService.gameGet(type, user, paginationRequest);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(3, TimeUnit.SECONDS))
            .body(CommonResponse.<Page<GameResponseDto>>builder().payload(games).message("").status(200).build());
    }

    @Operation(summary = "최근 본 게임 조회", description = "특정 방문자의 최근 본 게임 목록을 조회합니다.", parameters = {
        @Parameter(name = "visitorId", description = "방문자 ID", required = true, schema = @Schema(type = "String", example = "abc123"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 최근 본 게임 목록 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음")
    })
    @GetMapping("/recently")
    public ResponseEntity<CommonResponse<List<RecentlyViewGameDto>>> recentlyViewGames(@RequestHeader("visitorId") String visitorId) {
        List<RecentlyViewGameDto> dtos = likeGenreService.recentlyViewPosts(visitorId);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(3, TimeUnit.SECONDS))
            .body(CommonResponse.<List<RecentlyViewGameDto>>builder().payload(dtos).message("").status(200).build());
    }

}
