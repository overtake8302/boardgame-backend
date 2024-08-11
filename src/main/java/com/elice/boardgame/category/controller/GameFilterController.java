package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.BoardGameFilterDto;
import com.elice.boardgame.category.service.GameFilterService;
import com.elice.boardgame.common.dto.CommonResponse;
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
    public CommonResponse<List<GameResponseDto>> getFilteredBoardGames(BoardGameFilterDto filterDTO) {
        List<GameResponseDto> gameResponseDtos = gameFilterService.getFilteredBoardGames(filterDTO);
        return CommonResponse.<List<GameResponseDto>>builder()
            .payload(gameResponseDtos)
            .message("")
            .status(200)
            .build();
    }
}
