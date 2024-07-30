package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.GameRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRateRepository extends JpaRepository<GameRate, Long> {
    GameRate findByUserIdAndBoardGameGameId(Long gameId, Long id);
}
