package com.elice.boardgame.category.repository;

import java.util.List;
import java.util.Optional;
import com.elice.boardgame.category.entity.LiveViewRanking;
import com.elice.boardgame.game.entity.BoardGame;

public interface CustomLiveViewRankingRepository {
    Optional<LiveViewRanking> findByGame(BoardGame game);
    List<LiveViewRanking> findAllByOrderBySumScoreDesc();
}