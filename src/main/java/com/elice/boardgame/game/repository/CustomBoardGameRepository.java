package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomBoardGameRepository {
    List<BoardGame> findBoardGamesWithFilters(List<String> playTimes, List<String> playNums, List<String> ageLimits, List<String> prices, List<String> genres);
    List<BoardGame> findByGenres(List<Long> genreIds, Long userId);
    Page<GameResponseDto> findAllOrderByAverageRateDesc(Pageable pageable);
    GameResponseDto getGameResponseDtoByGameIdAndDeletedDateIsNull(Long gameId);
    List<GameResponseDto> findByNameContainingAndDeletedDateIsNull(String keyword);
    Page<GameResponseDto> findAllByDeletedDateIsNull(Pageable sortedPageable);
    List<GameResponseDto> findByGameGenresGenreGenre(String genre, Pageable pageable);
}