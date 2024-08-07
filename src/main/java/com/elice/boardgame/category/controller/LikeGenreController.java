package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.service.LikeGenreService;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.game.dto.GameResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public CommonResponse<List<GenreDto>> getGenres(@PathVariable Long userId) {
        List<GenreDto> genreDtos = likeGenreService.getGenres(userId);
        return CommonResponse.<List<GenreDto>>builder()
            .payload(genreDtos)
            .message("")
            .status(200)
            .build();
    }

    // 좋아요 누른 게임 가져오기
    @GetMapping("/user/game/like/{userId}")
    public CommonResponse<List<GameResponseDto>> getLikeGame(@PathVariable Long userId) {
        List<GameResponseDto> likedGames = likeGenreService.getLikeGames(userId);
        return CommonResponse.<List<GameResponseDto>>builder()
            .payload(likedGames)
            .message("")
            .status(200)
            .build();
    }

    //별점 준 게임 가져오기
    @GetMapping("/user/game/rate/{userId}")
    public CommonResponse<List<GameResponseDto>> getRateGame(@PathVariable Long userId) {
        List<GameResponseDto> rateGames =  likeGenreService.getRateGames(userId);
        return CommonResponse.<List<GameResponseDto>>builder()
            .payload(rateGames)
            .message("")
            .status(200)
            .build();
    }

    //좋아하는 장르 중 높은 평가를 받은 게임 (추천)
    @GetMapping("/user/genre/rate/{userId}")
    public CommonResponse<List<GameResponseDto>> getGenreGame(@PathVariable Long userId) {
        List<GameResponseDto> genreGames = likeGenreService.getGenreGame(userId);
        return CommonResponse.<List<GameResponseDto>>builder()
            .payload(genreGames)
            .message("")
            .status(200)
            .build();
    }

    //좋아하는 장르와 유사한 장르를 가진 게임 (추천)
    @GetMapping("/user/game/genre/{userId}")
    public CommonResponse<List<GameResponseDto>> getGamesFromLikeGenre(@PathVariable Long userId) {
        List<GameResponseDto> likeGames = likeGenreService.getGamesFromLikeGenre(userId);
        return CommonResponse.<List<GameResponseDto>>builder()
            .payload(likeGames)
            .message("")
            .status(200)
            .build();
    }
}
