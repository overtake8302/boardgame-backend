package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.service.GameFilterService;
import com.elice.boardgame.game.entity.BoardGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boardgames")
public class GameFilterController {

    @Autowired
    private GameFilterService gameFilterService;

    @GetMapping("/filter")
    public List<BoardGame> getFilteredBoardGames(
        @RequestParam(name = "playTimes", required = false) List<String> playTimes,
        @RequestParam(name = "playNums", required = false) List<String> playNums,
        @RequestParam(name = "ageLimits", required = false) List<String> ageLimits,
        @RequestParam(name = "prices", required = false) List<String> prices,
        @RequestParam(name = "genres", required = false) List<String> genres) {

        return gameFilterService.getFilteredBoardGames(playTimes, playNums, ageLimits, prices, genres);
    }


}
