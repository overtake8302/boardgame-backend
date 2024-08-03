package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomBoardGameRepository {
    List<BoardGame> findBoardGamesWithFilters(List<String> playTimes, List<String> playNums, List<String> ageLimits, List<String> prices, List<String> genres);
    List<BoardGame> findByGenres(List<Long> genreIds);
    Page<BoardGame> findAllOrderByAverageRateDesc(Pageable pageable);
    BoardGame findByGameIdAndDeletedDateIsNull(Long gameId);
    List<BoardGame> findByNameContainingAndDeletedDateIsNull(String keyword);
    Page<BoardGame> findAllByDeletedDateIsNull(Pageable sortedPageable);
    List<BoardGame> findByGameGenresGenreGenre(String genre, Pageable pageable);
}