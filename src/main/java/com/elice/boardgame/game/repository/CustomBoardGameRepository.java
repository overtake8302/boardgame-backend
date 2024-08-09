package com.elice.boardgame.game.repository;

import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomBoardGameRepository {
    List<BoardGame> findBoardGamesWithFilters(List<String> playTimes, List<String> playNums, List<String> ageLimits, List<String> prices, List<String> genres);
    Page<BoardGame> findByGenres(List<Long> genreIds, Long userId, Pageable pageable);
    GameResponseDto getGameResponseDtoByGameIdAndDeletedDateIsNull(Long gameId);
    Page<GameResponseDto> findByNameContainingAndDeletedDateIsNull(String keyword, Pageable pageable);
    Page<GameResponseDto> findAllByDeletedDateIsNull(Pageable pageable, Enums.GameListSortOption sortBy);
    List<GameResponseDto> findByGameGenresGenreGenre(String genre, Enums.GameListSortOption sort);

    Page<SearchResponse> searchByKeyword(String keyword, Pageable pageable);
    Page<GameResponseDto> findGamesLikedByUserId(Long userId, Pageable pageable);
}