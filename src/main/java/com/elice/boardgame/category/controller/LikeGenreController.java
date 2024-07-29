package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.entity.LikeGenreId;
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

    //게임 좋아요 추가
    @PutMapping("/like")
    public void addLikeGenreScore(@RequestBody LikeGenreId id) {
        likeGenreService.addLikeGenreScore(id);
    }

    //게임 좋아요 취소
    @PutMapping("/unlike")
    public void subtractLikeGenreScore(@RequestBody LikeGenreId id) {
        likeGenreService.subtractLikeGenreScore(id);
    }

    //게임 평점 추가
    @PutMapping("/rate/{rating}")
    public void addRateGenreScore(@RequestBody LikeGenreId id, @PathVariable int rating) {
        likeGenreService.addRateGenreScore(id, rating);
    }

    //게임 평점 취소
    @PutMapping("/unrate/{rating}")
    public void subtractRateGenreScore(@RequestBody LikeGenreId id, @PathVariable int rating) {
        likeGenreService.subtractRateGenreScore(id, rating);
    }

    //멤버별 좋아하는 장르 가져오기
    @GetMapping("/user/{userId}")
    public String getGenresByMemberId(@PathVariable Long userId) {
        return likeGenreService.getTop3GenreIds(userId).toString();   // 프론트에 어떻게 보내야할지 모르겠음.
    }

    // 좋아하는 장르중 높은 평가를 받은 게임
    @GetMapping("/user/genre/rate/{userId}")
    public List<BoardGame> getGenreGame(@PathVariable Long userId) {
        return likeGenreService.getGenreGame(userId);
    }
}
