package com.elice.boardgame.game.repository;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.category.DTO.BoardGameRateDto;
import com.elice.boardgame.category.DTO.RatingCountDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameRate;
import java.util.List;
import java.util.Optional;

public interface CustomGameRateRepository {

    Double findAverageRateByBoardGame(BoardGame game);

    List<RatingCountDto> countRatingsByUserId(Long userId);

    List<BoardGameRateDto> findByUserIdAndRate(Long userId, Double rate);

    Optional<GameRate> findByUserAndBoardGame(User user, BoardGame boardGame);

    void deleteByUserAndBoardGame(User user, BoardGame boardGame);

    List<BoardGame> findByUserId(Long userId);
}