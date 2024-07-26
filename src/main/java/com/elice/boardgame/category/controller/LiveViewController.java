package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.service.LiveViewService;
import com.elice.boardgame.game.entity.BoardGame;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    //뷰 스코어 업데이트 (하루에한번 자동으로 설정해놓는게 필요함)
    @PutMapping("/update-score")
    public void updateLiveViewScore() {
        liveViewService.updateLiveViewScore();
    }


    //뷰 랭킹 업데이트
    @PutMapping("/update-ranking")
    public void updateRanking() {
        liveViewService.updateRanking();
    }

    //뷰 랭킹순 가져오기 // 전부 가져가서 프런트에서 깎아줄지 탑 10으로 정해지면 탑 10만 정해서 보내줄지, 아마 후자가 좋을것같음
    @GetMapping("/getList")
    public List<BoardGame> getLiveViewRanking() {
        return liveViewService.getLiveViewRanking();
    }
}
