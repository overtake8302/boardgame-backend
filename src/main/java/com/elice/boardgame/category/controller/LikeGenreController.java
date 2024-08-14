package com.elice.boardgame.category.controller;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.dto.RecentlyViewGameDto;
import com.elice.boardgame.category.service.LikeGenreService;
import com.elice.boardgame.common.annotation.CurrentUser;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.game.dto.GameResponseDto;
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

    // 유저 좋아요 처리
    @PutMapping("/like/{gameId}")
    public Boolean handleLike(@CurrentUser User user, @PathVariable Long gameId) {
        return likeGenreService.likeGenreScore(user.getId(), gameId);
    }

    // 유저 평점 처리
    @PutMapping("/rate/{gameId}/{rating}")
    public Boolean handleRateScore(@CurrentUser User user, @PathVariable Long gameId,
        @PathVariable Double rating) {
        return likeGenreService.genreRatingScore(user.getId(), gameId, rating);
    }

    //유저별 좋아하는 장르 가져오기
    @GetMapping("/user/genre")
    public ResponseEntity<CommonResponse<List<GenreDto>>> getGenres(@CurrentUser User user) {
        List<GenreDto> genreDtos = likeGenreService.getGenres(user.getId());
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
            .body(CommonResponse.<List<GenreDto>>builder()
                .payload(genreDtos)
                .message("")
                .status(200)
                .build());
    }

    @GetMapping("/game")
    public ResponseEntity<CommonResponse<Page<GameResponseDto>>> getGames(
        @RequestParam String type,
        @CurrentUser User user,
        PaginationRequest paginationRequest) {

        Page<GameResponseDto> games = likeGenreService.gameGet(type, user, paginationRequest);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
            .body(CommonResponse.<Page<GameResponseDto>>builder()
                .payload(games)
                .message("")
                .status(200)
                .build());
    }

    @GetMapping("/recently")
    public ResponseEntity<CommonResponse<List<RecentlyViewGameDto>>> recentlyViewGames(@RequestHeader("visitorId") String visitorId) {
        List<RecentlyViewGameDto> dtos = likeGenreService.recentlyViewPosts(visitorId);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
            .body(CommonResponse.<List<RecentlyViewGameDto>>builder()
                .payload(dtos)
                .message("")
                .status(200)
                .build());
    }
}
