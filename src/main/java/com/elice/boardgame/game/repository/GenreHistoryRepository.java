package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.GameGenreHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreHistoryRepository extends JpaRepository<GameGenreHistory, Long> {
}
