package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.DTO.GenreDto;
import com.elice.boardgame.category.service.LikeGenreService;
import com.elice.boardgame.game.dto.GameResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like-genre")
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
    public List<GenreDto> getGenres(@PathVariable Long userId) {
        return likeGenreService.getGenres(userId);   // 프론트에서는 탑3만 보여줘야함
    }

    //좋아요 누른 게임 가져오기
    @GetMapping("/user/game/like/{userId}")
    public List<GameResponseDto> getLikeGame(@PathVariable Long userId) {
        return likeGenreService.getLikeGames(userId);
    }

    //별점 준 게임 가져오기
    @GetMapping("/user/game/rate/{userId}")
    public List<GameResponseDto> getRateGame(@PathVariable Long userId) {
        return likeGenreService.getRateGames(userId);
    }

    //좋아하는 장르 중 높은 평가를 받은 게임 (추천)
    @GetMapping("/user/genre/rate/{userId}")
    public List<GameResponseDto> getGenreGame(@PathVariable Long userId) {
        return likeGenreService.getGenreGame(userId);
    }

    //좋아하는 장르와 유사한 장르를 가진 게임 (추천)
    @GetMapping("/user/game/genre/{userId}")
    public List<GameResponseDto> getGamesFromLikeGenre(@PathVariable Long userId) {
        return likeGenreService.getGamesFromLikeGenre(userId);
    }
}
