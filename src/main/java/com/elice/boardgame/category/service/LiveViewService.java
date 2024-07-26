package com.elice.boardgame.category.service;

import com.elice.boardgame.category.entity.BoardGameEntity;
import com.elice.boardgame.category.entity.LiveViewEntity;
import com.elice.boardgame.category.repository.LiveViewRepository;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LiveViewService {

    private final LiveViewRepository liveViewRepository;

    public void addViewCount(BoardGameEntity game, String ipAddress) {
        LiveViewEntity liveViewEntity = new LiveViewEntity();
        liveViewEntity.setGame(game);
        liveViewEntity.setCreatedDate(new Date());
        liveViewEntity.setViewCount(1L);
        liveViewEntity.setIpAddress(ipAddress);

        liveViewRepository.save(liveViewEntity);
    }

//    매일 한번씩
//    Date >= current.date - Interval '7 day' then 1
//    Date >= current.date - Interval '14 day' then 0.5
//    Date >= current.date - Interval '21 day' then 0.25
//    Date >= current.date - Interval '30 day' then delete
//    Date >= current.date - Interval '나머지' then 0.1
//    실행해야함
}
