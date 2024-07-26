package com.elice.boardgame.game.service;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameProfilePic;
import com.elice.boardgame.game.exception.GameDeleteFailException;
import com.elice.boardgame.game.exception.GameNotFoundException;
import com.elice.boardgame.game.repository.BoardGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardGameService {

    private final BoardGameRepository boardGameRepository;
    private final GameProfilePicService gameProfilePicService;

    @Transactional
    public BoardGame create(BoardGame newBoardGame) {

        BoardGame savedBoardGame = boardGameRepository.save(newBoardGame);

        return savedBoardGame;
    }

    @Transactional
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

    @Transactional
    public void deleteGameByGameId(Long gameId) {

        BoardGame targetGame = findGameByGameId(gameId);
        List<GameProfilePic> targetPics = targetGame.getGameProfilePics();

        try {
            for (GameProfilePic pic : targetPics) {
                gameProfilePicService.deleteByFileName(pic);
            }

            targetGame.setGameProfilePics(Collections.emptyList());
            targetGame.setDeletedAt(LocalDateTime.now());
            boardGameRepository.save(targetGame);

        } catch (Exception e) {
            throw new GameDeleteFailException();
        }
    }

    @Transactional
    public BoardGame editWithoutPics(BoardGame target) {

        gameProfilePicService.deleteFiles(target.getGameProfilePics());
        target.setGameProfilePics(Collections.emptyList());

        BoardGame updatedGame = boardGameRepository.save(target);

        return updatedGame;
    }

    @Transactional
    public BoardGame editWithPics(BoardGame target, List<MultipartFile> files) throws IOException {

        gameProfilePicService.deleteFiles(target.getGameProfilePics());
        target.setGameProfilePics(Collections.emptyList());

        List<GameProfilePic> pics = new ArrayList<>();

        for (MultipartFile file : files) {
            GameProfilePic pic = gameProfilePicService.save(file);
            pics.add(pic);
        }

        target.setGameProfilePics(pics);

        boardGameRepository.save(target);

        return target;
    }


    public List<BoardGame> findGameByName(String keyword) {

        List<BoardGame> foundGames = boardGameRepository.findByNameContaining(keyword);

        if (foundGames == null || foundGames.isEmpty()) {
            throw new GameNotFoundException();
        }

        return foundGames;
    }
}
