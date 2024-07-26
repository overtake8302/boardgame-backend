package com.elice.boardgame.game.mapper;

import com.elice.boardgame.game.dto.GameProfilePicResponseDto;
import com.elice.boardgame.game.entity.GameProfilePic;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameProfilePicMapper {

    public GameProfilePicResponseDto gameProfilePicToDto(List<GameProfilePic> pics) {

        GameProfilePicResponseDto dto = new GameProfilePicResponseDto();
        List<String> urls = new ArrayList<>();

        for (GameProfilePic pic : pics) {
            String url = pic.getPicAddress();
            urls.add(url);
        }

        dto.setPicAddress(urls);

        return dto;
    }
}
