package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.GameVisitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameVisitorRepository extends JpaRepository<GameVisitor, Long>, CustomGameVisitorRepository {
    GameVisitor findByIdVisitorIdAndIdGameId(String visitorId, Long gameId);
    int countByIdGameId(Long gameId);
}
