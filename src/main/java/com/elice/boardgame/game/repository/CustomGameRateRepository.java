package com.elice.boardgame.game.repository;

import com.elice.boardgame.category.DTO.BoardGameRateDTO;
import com.elice.boardgame.category.DTO.RatingCountDTO;
import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;

public interface CustomGameRateRepository {
    Double findAverageRateByBoardGame(BoardGame game);
    List<RatingCountDTO> countRatingsByUserId(Long userId);
    List<BoardGameRateDTO> findByUserIdAndRate(Long userId, Double rate);
}
