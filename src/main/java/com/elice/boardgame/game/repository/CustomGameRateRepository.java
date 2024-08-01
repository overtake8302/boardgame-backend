package com.elice.boardgame.game.repository;

import com.elice.boardgame.category.DTO.BoardGameRateDto;
import com.elice.boardgame.category.DTO.RatingCountDto;
import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;

public interface CustomGameRateRepository {
    Double findAverageRateByBoardGame(BoardGame game);
    List<RatingCountDto> countRatingsByUserId(Long userId);
    List<BoardGameRateDto> findByUserIdAndRate(Long userId, Double rate);
}
