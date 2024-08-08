package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomGameGenreRepository {

    Page<BoardGame> findBoardGamesByGenresOrderByRatingDesc(List<Genre> genres, Long userId, Pageable pageable);

    Optional<List<Genre>> findGenresByBoardGame(BoardGame boardGame);
}
