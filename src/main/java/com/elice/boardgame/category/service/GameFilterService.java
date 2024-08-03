package com.elice.boardgame.category.service;

import com.elice.boardgame.category.DTO.BoardGameFilterDto;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.repository.BoardGameRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameFilterService {

    private final BoardGameRepository boardGameRepository;

    private final BoardGameMapper boardGameMapper;

    public List<GameResponseDto> getFilteredBoardGames(BoardGameFilterDto filterDTO) {

        List<BoardGame> boardGames = boardGameRepository.findBoardGamesWithFilters(
            filterDTO.getPlayTimes(),
            filterDTO.getPlayNums(),
            filterDTO.getAgeLimits(),
            filterDTO.getPrices(),
            filterDTO.getGenres()
        );

        List<GameResponseDto> dtos = new ArrayList<>();
        for (BoardGame game : boardGames) {
            dtos.add(boardGameMapper.boardGameToGameResponseDto(game));
        }

        return dtos;
    }
}
