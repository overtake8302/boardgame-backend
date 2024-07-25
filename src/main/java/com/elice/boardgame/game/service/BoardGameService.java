package com.elice.boardgame.game.service;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameProfilePic;
import com.elice.boardgame.game.exception.GameNotFoundException;
import com.elice.boardgame.game.repository.BoardGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardGameService {

    private final BoardGameRepository boardGameRepository;
    private final GameProfilePicService gameProfilePicService;


    public BoardGame create(BoardGame newBoardGame) {

        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        return savedBoardGame;
    }

    public BoardGame create(BoardGame newBoardGame, List<MultipartFile> files) throws IOException {

        List<GameProfilePic> pics = new ArrayList<>();

        for (MultipartFile file : files) {
            GameProfilePic pic = gameProfilePicService.save(file);
            pics.add(pic);
        }

        newBoardGame.setGameProfilePics(pics);

        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        return savedBoardGame;
    }

    public BoardGame findGameByGameId(Long gameId) {

        BoardGame foundGame = boardGameRepository.findByGameIdAndDeletedAtIsNull(gameId);

        if (foundGame == null) {
            throw new GameNotFoundException();
        }

        return foundGame;
    }
}
