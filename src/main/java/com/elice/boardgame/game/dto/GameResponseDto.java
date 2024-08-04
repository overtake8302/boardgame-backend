package com.elice.boardgame.game.dto;

import com.elice.boardgame.category.entity.GameGenre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GameResponseDto {

    private Long gameId;

    private String name;

    private String playTime;

    private String playNum;

    private String ageLimit;

    private int price;

    private String designer;

    private String artwork;

    private String releaseDate;

    private String difficulty;

    private String publisher;

    private String youtubeLink;

    private int likeCount;

    private Double averageRate;

    private GameProfilePicResponseDto gameProfilePics;

    private List<GameGenre> gameGenres = new ArrayList<>();

    private Long views;
}
