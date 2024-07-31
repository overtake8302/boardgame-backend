package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.DTO.LikeRequest;
import com.elice.boardgame.category.DTO.RatingRequest;
import com.elice.boardgame.category.service.LikeGenreService;
import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like-genre")
@RequiredArgsConstructor
public class LikeGenreController {

    private final LikeGenreService likeGenreService;

    // 유저 좋아요 처리
    @PutMapping("/like")
    public void handleLike(@RequestBody LikeRequest request) {
        if (!request.getLike()) {
            likeGenreService.addLikeGenreScore(request.getId());
            request.setLike(true);
        } else {
            likeGenreService.subtractLikeGenreScore(request.getId());
            request.setLike(false);
        }
    }

    // 유저 평점 처리
    @PutMapping("/rate")
    public void addRateGenreScore(@RequestBody RatingRequest request) {
        if (!request.getCheck()) {
            likeGenreService.addRateGenreScore(request.getId(), request.getRating());
            request.setCheck(true);
        } else {
            likeGenreService.subtractRateGenreScore(request.getId(), request.getRating());
            request.setCheck(false);
        }
    }

    //유저별 좋아하는 장르 가져오기
    @GetMapping("/user/genre/{userId}")
    public String getGenresByUserId(@PathVariable Long userId) {
        return likeGenreService.getGenres(userId).toString();   // 프론트에서는 탑3만 보여줘야함
    }

    //좋아요 누른 게임 가져오기
    @GetMapping("/user/game/{userId}")
    public List<BoardGame> getGameByUserId(@PathVariable Long userId) {
        return likeGenreService.getGames(userId);
    }

    //좋아하는 장르 중 높은 평가를 받은 게임 (추천)
    @GetMapping("/user/genre/rate/{userId}")
    public List<BoardGame> getGenreGame(@PathVariable Long userId) {
        return likeGenreService.getGenreGame(userId);
    }

    //좋아하는 장르와 유사한 장르를 가진 게임 (추천)
    @GetMapping("/user/game/genre/{userId}")
    public List<BoardGame> getGamesFromLikeGenre(@PathVariable Long userId) {
        return likeGenreService.getGamesFromLikeGenre(userId);
    }
}
