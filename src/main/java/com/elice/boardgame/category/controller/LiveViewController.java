package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.service.LiveViewService;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/LiveView")
@RequiredArgsConstructor
public class LiveViewController {

    private final LiveViewService liveViewService;

    //뷰 스코어 추가
    @PutMapping("/add")
    public void addViewScore(@RequestBody BoardGame game, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        liveViewService.addViewScore(game, ipAddress);
    }

    @Scheduled(cron = "0 0 4 * * ?")
    @PutMapping("/update-score")
    public void updateLiveViewScore() {
        liveViewService.updateLiveViewScore();
        liveViewService.updateRanking();
    }

    //뷰 랭킹순 가져오기
    @GetMapping("/list")
    public List<GameResponseDto> getLiveViewRanking() {
        return liveViewService.getLiveViewRanking();
    }
}
