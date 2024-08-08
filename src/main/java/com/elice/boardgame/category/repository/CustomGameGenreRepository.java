package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;
import java.util.Optional;

public interface CustomGameGenreRepository {

    Optional<List<BoardGame>> findBoardGamesByGenresOrderByRatingDesc(List<Genre> genres, Long userId);

    Optional<List<Genre>> findGenresByBoardGame(BoardGame boardGame);
}
