package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGameHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardGameHistoryRepository extends JpaRepository<BoardGameHistory, Long>, CustomBoardGameHistoryRepository {
}
