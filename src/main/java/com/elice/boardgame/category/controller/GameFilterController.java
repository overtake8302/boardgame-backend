package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.DTO.BoardGameFilterDTO;
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
    public List<BoardGame> getFilteredBoardGames(BoardGameFilterDTO filterDTO) {
        return gameFilterService.getFilteredBoardGames(
            filterDTO.getPlayTimes(),
            filterDTO.getPlayNums(),
            filterDTO.getAgeLimits(),
            filterDTO.getPrices(),
            filterDTO.getGenres()
        );
    }
}
