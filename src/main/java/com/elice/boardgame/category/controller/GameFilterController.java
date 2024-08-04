package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.DTO.BoardGameFilterDto;
import com.elice.boardgame.category.service.GameFilterService;
import com.elice.boardgame.game.dto.GameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boardgames")
@RequiredArgsConstructor
public class GameFilterController {

    private final GameFilterService gameFilterService;

    @GetMapping("/filter")
    public List<GameResponseDto> getFilteredBoardGames(BoardGameFilterDto filterDTO) {
        return gameFilterService.getFilteredBoardGames(filterDTO);
    }
}
