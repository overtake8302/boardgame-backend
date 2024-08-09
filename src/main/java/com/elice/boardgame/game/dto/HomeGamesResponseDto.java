package com.elice.boardgame.game.dto;

import com.elice.boardgame.category.entity.GameGenre;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class HomeGamesResponseDto {

    private Long gameId;

    private String name;

    private String difficulty;

    private int likeCount;

    private Double averageRate;

    private List<String> gameProfilePics;

    private Long views;

    private List<GameGenre> gameGenres = new ArrayList<>();
}
