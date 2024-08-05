package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameRate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRateRepository extends JpaRepository<GameRate, Long>, CustomGameRateRepository {
    GameRate findByUserIdAndBoardGameGameId(Long userId, Long gameId);
}
