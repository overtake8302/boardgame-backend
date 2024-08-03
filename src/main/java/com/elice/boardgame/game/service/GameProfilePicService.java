package com.elice.boardgame.game.service;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameProfilePic;
import com.elice.boardgame.game.repository.GameProfilePIcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameProfilePicService {

    private final GameProfilePIcRepository gameProfilePIcRepository;
    private final BoardGameS3Service boardGameS3Service;

    public GameProfilePic save(MultipartFile file, BoardGame savedBoardGame) throws IOException {

        List<String> info = boardGameS3Service.uploadFileToBucket1(file);

        GameProfilePic gameProfilePic = new GameProfilePic();
        gameProfilePic.setBoardGame(savedBoardGame);
        gameProfilePic.setFileName(info.get(0));
        gameProfilePic.setPicAddress(info.get(1));
        GameProfilePic savedGameProfilePic = gameProfilePIcRepository.save(gameProfilePic);

        return savedGameProfilePic;
    }

    @Transactional
    public void deleteByFileName(GameProfilePic pic) {

        boardGameS3Service.deleteFileFromBucket1(pic.getFileName());
        gameProfilePIcRepository.deleteByPicId(pic.getPicId());
    }

    @Transactional
    public void deleteFiles(List<GameProfilePic> gameProfilePics) {

        for (GameProfilePic pic : gameProfilePics) {
            deleteByFileName(pic);
        }
    }
}
