package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.entity.BoardGameEntity;
import com.elice.boardgame.category.service.LiveViewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/LiveView")
@RequiredArgsConstructor
public class LiveViewController {

    private final LiveViewService liveViewService;

    @PutMapping("/add")
    public void addViewCount(@RequestBody BoardGameEntity game, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        liveViewService.addViewCount(game, ipAddress);
    }
}
