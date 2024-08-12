package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.BoardGameFilterDto;
import com.elice.boardgame.category.service.GameFilterService;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.game.dto.GameResponseDto;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boardgames")
@RequiredArgsConstructor
public class GameFilterController {

    private final GameFilterService gameFilterService;

    @GetMapping("/filter")
    public ResponseEntity<CommonResponse<List<GameResponseDto>>> getFilteredBoardGames(BoardGameFilterDto filterDTO) {
        List<GameResponseDto> gameResponseDtos = gameFilterService.getFilteredBoardGames(filterDTO);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .body(CommonResponse.<List<GameResponseDto>>builder()
                .payload(gameResponseDtos)
                .message("")
                .status(200)
                .build());
    }
}
