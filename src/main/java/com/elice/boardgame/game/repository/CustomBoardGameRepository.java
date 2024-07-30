package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;

public interface CustomBoardGameRepository {
    List<BoardGame> findBoardGamesWithFilters(List<String> playTimes, List<String> playNums, List<String> ageLimits, List<String> prices, List<String> genres);
}