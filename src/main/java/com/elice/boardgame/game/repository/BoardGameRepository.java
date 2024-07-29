package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardGameRepository extends JpaRepository<BoardGame, Long>, BoardGameRepositoryCustom {

    @Query("select count(gl) from GameLike gl where gl.boardGame.gameId = :gameId")
    int countLikesByGameId(@Param("gameId") Long gameId);

    BoardGame findByGameIdAndDeletedAtIsNull(Long gameId);
}
