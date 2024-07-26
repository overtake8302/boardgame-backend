package com.elice.boardgame.game.repository;

import com.elice.boardgame.category.entity.GameGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameGenreRepository extends JpaRepository<GameGenre, Long> {

    void deleteById_GameIdAndId_GenreId(Long gameId, Long genreId);
}
