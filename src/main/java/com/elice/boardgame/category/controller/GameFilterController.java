package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.BoardGameFilterDto;
import com.elice.boardgame.category.service.GameFilterService;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.game.dto.GameResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "게임 필터 조회", description = "다수의 카테고리로 게임을 필터링하여 조회합니다.", parameters = {
        @Parameter(name = "playTimes", description = "게임 시간", required = false, schema = @Schema(type = "List<String>", example = "[\"30분 이하\", \"1시간\"]")),
        @Parameter(name = "playNums", description = "권장 인원", required = false, schema = @Schema(type = "List<String>", example = "[\"2명\", \"4명\"]")),
        @Parameter(name = "ageLimits", description = "연령 제한", required = false, schema = @Schema(type = "List<String>", example = "[\"12세 이상\", \"18세 이상\"]")),
        @Parameter(name = "prices", description = "가격", required = false, schema = @Schema(type = "List<String>", example = "[\"무료\", \"5000원 이하\"]")),
        @Parameter(name = "genres", description = "장르", required = false, schema = @Schema(type = "List<String>", example = "[\"액션\", \"퍼즐\"]"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 필터링된 게임 목록 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/filter")
    public ResponseEntity<CommonResponse<List<GameResponseDto>>> getFilteredBoardGames(BoardGameFilterDto filterDTO) {
        List<GameResponseDto> gameResponseDtos = gameFilterService.getFilteredBoardGames(filterDTO);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(3, TimeUnit.SECONDS))
            .body(CommonResponse.<List<GameResponseDto>>builder()
                .payload(gameResponseDtos)
                .message("")
                .status(200)
                .build());
    }

}
