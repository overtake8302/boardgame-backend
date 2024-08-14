package com.elice.boardgame.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameListResponseDto {

    private Long gameId;

    private String name;

    private String difficulty;

    private int likeCount;

    private Double averageRate;

    private List<String> gameProfilePics;

    private Long views;
}
