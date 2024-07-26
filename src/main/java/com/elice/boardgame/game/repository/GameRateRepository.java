package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.GameRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRateRepository extends JpaRepository<GameRate, Long> {

    @Query("select avg(gr.rate) from GameRate gr where gr.boardGame.gameId = :gameId")
    Double findAverageRateByGameId(@Param("gameId") Long gameId);
}
