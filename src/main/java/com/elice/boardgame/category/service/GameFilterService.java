package com.elice.boardgame.category.service;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.repository.BoardGameRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameFilterService {

    private final BoardGameRepository boardGameRepository;

    public List<BoardGame> getFilteredBoardGames(List<String> playTimes, List<String> playNums, List<String> ageLimits, List<String> prices, List<String> genres) {
        return boardGameRepository.findBoardGamesWithFilters(playTimes, playNums, ageLimits, prices, genres);
    }
}
