package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardGameRepository extends JpaRepository<BoardGame, Long>, CustomBoardGameRepository {
    BoardGame findByGameIdAndDeletedAtIsNull(Long gameId);
}
