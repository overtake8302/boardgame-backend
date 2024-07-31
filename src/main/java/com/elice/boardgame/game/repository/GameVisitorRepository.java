package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.GameVisitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameVisitorRepository extends JpaRepository<GameVisitor, Long> {
    GameVisitor findByVisitorIdAndBoardGameGameId(String visitorId, Long gameId);
    int countByBoardGameGameId(Long gameId);
}
