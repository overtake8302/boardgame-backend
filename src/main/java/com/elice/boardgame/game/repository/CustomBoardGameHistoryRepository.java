package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.dto.GameHistoriesResponseDto;
import com.elice.boardgame.game.dto.GameHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBoardGameHistoryRepository {

    Page<GameHistoriesResponseDto> findHistoriesByGameId(Pageable pageable, Long gameId);
    GameHistoryResponseDto findHistoryByHistoryId(Long historyId);
}
