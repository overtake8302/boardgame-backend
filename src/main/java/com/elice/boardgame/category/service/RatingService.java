package com.elice.boardgame.category.service;

import com.elice.boardgame.category.DTO.BoardGameRateDTO;
import com.elice.boardgame.category.DTO.RatingCountDTO;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.repository.GameRateRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final GameRateRepository gameRateRepository;


    public List<RatingCountDTO> getRatingCounts(Long userId) {
        return gameRateRepository.countRatingsByUserId(userId);
    }

    public List<BoardGame> getGamesByRating(Long userId, Double rate) {
        List<BoardGameRateDTO> dto = gameRateRepository.findByUserIdAndRate(userId, rate);
        return dto.stream()
            .map(BoardGameRateDTO::getBoardGame)
            .collect(Collectors.toList());
    }

}
