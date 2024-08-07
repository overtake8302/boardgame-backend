package com.elice.boardgame.game.mapper;

import org.springframework.stereotype.Component;

@Component
public class GameProfilePicMapper {

    /*public GameProfilePicResponseDto gameProfilePicToDto(List<GameProfilePic> pics) {

        GameProfilePicResponseDto dto = new GameProfilePicResponseDto();
        List<String> urls = new ArrayList<>();

        for (GameProfilePic pic : pics) {
            String url = pic.getPicAddress();
            urls.add(url);
        }

        dto.setPicAddress(urls);

        return dto;
    }*/
}
