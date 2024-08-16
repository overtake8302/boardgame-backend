package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.GenreDto;
import com.elice.boardgame.category.service.LiveViewService;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
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

    //뷰 스코어 추가
    @PutMapping("/add")
    public void addViewScore(@RequestParam Long gameId, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        liveViewService.addViewScore(gameId, ipAddress);
    }

    //정각 한시간마다 업데이트
    @Scheduled(cron = "0 0 * * * ?")
    @PutMapping("/score")
    public void updateLiveViewScore() {
        liveViewService.updateViewScores();
        liveViewService.updateRanking();
    }

    //뷰 랭킹순 가져오기
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
