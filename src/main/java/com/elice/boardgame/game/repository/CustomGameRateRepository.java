package com.elice.boardgame.game.repository;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.category.dto.RatingCountDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameRate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomGameRateRepository {

    Double findAverageRateByBoardGame(BoardGame game);

    List<RatingCountDto> countRatingsByUserId(Long userId);

    RatingCountDto findRatingCountByUserIdAndRate(Long userId, Double rate);

    Optional<GameRate> findByUserAndBoardGame(User user, BoardGame boardGame);

    void deleteByUserAndBoardGame(User user, BoardGame boardGame);

    Page<BoardGame> findByUserId(Long userId, Pageable pageable);
}