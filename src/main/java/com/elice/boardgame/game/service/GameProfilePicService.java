package com.elice.boardgame.game.service;

import com.elice.boardgame.game.entity.GameProfilePic;
import com.elice.boardgame.game.repository.GameProfilePIcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GameProfilePicService {

    private final GameProfilePIcRepository gameProfilePIcRepository;
    private final BoardGameS3Service boardGameS3Service;

    public GameProfilePic save(MultipartFile file) throws IOException {

        String picUrl = boardGameS3Service.uploadFileToBucket1(file);

        GameProfilePic gameProfilePic = new GameProfilePic();
        gameProfilePic.setPicAddress(picUrl);
        GameProfilePic savedGameProfilePic = gameProfilePIcRepository.save(gameProfilePic);

        return savedGameProfilePic;
    }
}
