package com.elice.boardgame.game.service;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.repository.BoardGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardGameService {

    private final BoardGameRepository boardGameRepository;


    public BoardGame create(BoardGame newBoardGame) {

        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        return savedBoardGame;
    }
}
