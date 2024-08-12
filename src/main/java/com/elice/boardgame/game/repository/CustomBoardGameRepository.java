package com.elice.boardgame.game.repository;

import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.game.dto.GameListResponseDto;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.dto.HomeGamesResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.post.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomBoardGameRepository {
    List<BoardGame> findBoardGamesWithFilters(List<String> playTimes, List<String> playNums, List<String> ageLimits, List<String> prices, List<String> genres);
    Page<BoardGame> findByGenres(List<Long> genreIds, Long userId, Pageable pageable);
    GameResponseDto getGameResponseDtoByGameIdAndDeletedAtIsNull(Long gameId);
    Page<GameResponseDto> findByNameContainingAndDeletedAtIsNull(String keyword, Pageable pageable);
    Page<GameListResponseDto> findAllByDeletedAtIsNull(Pageable pageable, Enums.GameListSortOption sortBy);
    List<HomeGamesResponseDto> findByGameGenresGenreGenre(Enums.GameListSortOption sort, String genre);

    Page<SearchResponse> searchByKeyword(String keyword, Pageable pageable);
    Page<GameResponseDto> findGamesLikedByUserId(Long userId, Pageable pageable);

    List<CommentDto> findComentsByGameId(Long gameId);

    Page<GameListResponseDto> findByNameContainingAndDeletedAtIsNull(Pageable pageable, Enums.GameListSortOption sortBy, String keyword);
}