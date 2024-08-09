package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.service.LikeGenreService;
import com.elice.boardgame.common.dto.CommonResponse;
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
    @PutMapping("/like/{gameId}/{userId}")
    public void handleLike(@PathVariable Long userId, @PathVariable Long gameId) {
        likeGenreService.likeGenreScore(userId, gameId);
    }

    // 유저 평점 처리
    @PutMapping("/rate/{gameId}/{userId}/{rating}")
    public void addRateGenreScore(@PathVariable Long userId, @PathVariable Long gameId,
        @PathVariable Double rating) {
        likeGenreService.genreRatingScore(userId, gameId, rating);
    }

    //유저별 좋아하는 장르 가져오기
    @GetMapping("/user/genre/{userId}")
    public ResponseEntity<CommonResponse<List<GenreDto>>> getGenres(@PathVariable Long userId) {
        List<GenreDto> genreDtos = likeGenreService.getGenres(userId);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .body(CommonResponse.<List<GenreDto>>builder()
                .payload(genreDtos)
                .message("")
                .status(200)
                .build());
    }

    @GetMapping("/game")
    public ResponseEntity<CommonResponse<Page<GameResponseDto>>> getGames(
        @RequestParam String type,
        @RequestParam Long userId,
        @RequestParam int page,
        @RequestParam int size) {

        Page<GameResponseDto> games;

        switch (type) {
            case "like":
                games = likeGenreService.getLikeGames(userId, page, size);
                break;
            case "rate":
                games = likeGenreService.getRateGames(userId, page, size);
                break;
            case "genre/rate":
                games = likeGenreService.getGenreGame(userId, page, size);
                break;
            case "genre":
                games = likeGenreService.getGamesFromLikeGenre(userId, page, size);
                break;
            default:
                return ResponseEntity.badRequest().body(
                    CommonResponse.<Page<GameResponseDto>>builder()
                        .payload(Page.empty())
                        .message("Invalid type parameter")
                        .status(400)
                        .build()
                );
        }

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .body(CommonResponse.<Page<GameResponseDto>>builder()
                .payload(games)
                .message("")
                .status(200)
                .build());
    }

}
