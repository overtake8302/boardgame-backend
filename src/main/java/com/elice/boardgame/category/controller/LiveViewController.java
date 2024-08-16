package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.service.LiveViewService;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/live")
@RequiredArgsConstructor
public class LiveViewController {

    private final LiveViewService liveViewService;

    @Operation(summary = "live-view score 추가", description = "실시간 인기순위 테이블에 해당 게임의 스코어를 추가합니다", parameters = {
        @Parameter(name = "gameId", description = "방문한 게임 아이디", required = true, schema = @Schema(type = "Long", example = "1"))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 스코어 추가됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음")
    })
    @PutMapping("/add")
    public void addViewScore(@RequestParam Long gameId, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        liveViewService.addViewScore(gameId, ipAddress);
    }

    @Operation(summary = "live-view-ranking 업데이트", description = "한시간마다 live-view 테이블의 방문한 시간이 지남에 따라 view score를 업데이트하고 live-view-ranking의 순위를 변동시킵니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 순위 업데이트됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @Scheduled(cron = "0 0 * * * ?")
    @PutMapping("/score")
    public void updateLiveViewScore() {
        liveViewService.updateLiveViewScore();
        liveViewService.updateRanking();
    }

    @Operation(summary = "인기순위 목록 조회", description = "실시간 인기순위 게임 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 인기순위 목록 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping()
    public ResponseEntity<CommonResponse<List<GameResponseDto>>> getLiveViewRanking() {
        List<GameResponseDto> gameResponseDtos = liveViewService.getLiveViewRanking();
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .body(CommonResponse.<List<GameResponseDto>>builder()
                .payload(gameResponseDtos)
                .message("")
                .status(200)
                .build());
    }
}
