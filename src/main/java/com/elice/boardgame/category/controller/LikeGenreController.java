package com.elice.boardgame.category.controller;


import com.elice.boardgame.category.entity.LikeGenreId;
import com.elice.boardgame.category.service.LikeGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like-genre")
@RequiredArgsConstructor
public class LikeGenreController {

    private final LikeGenreService likeGenreService;

    @PutMapping("/like")
    public void addLikeGenreScore(@RequestBody LikeGenreId id) {
        likeGenreService.addLikeGenreScore(id);
    }

    @PutMapping("/unlike")
    public void subtractLikeGenreScore(@RequestBody LikeGenreId id) {
        likeGenreService.subtractLikeGenreScore(id);
    }

    @PutMapping("/rate/{rating}")
    public void addRateGenreScore(@RequestBody LikeGenreId id, @PathVariable int rating) {
        likeGenreService.addRateGenreScore(id, rating);
    }

    @PutMapping("/unrate/{rating}")
    public void subtractRateGenreScore(@RequestBody LikeGenreId id, @PathVariable int rating) {
        likeGenreService.subtractRateGenreScore(id, rating);
    }
}
