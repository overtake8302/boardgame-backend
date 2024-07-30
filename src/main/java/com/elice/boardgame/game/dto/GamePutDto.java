package com.elice.boardgame.game.dto;

import com.elice.boardgame.enums.Enums;
import com.elice.boardgame.game.entity.BoardGame;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GamePutDto {

    private Long gameId;

    private String name;

    private Enums.PlayTime playTime;

    private Enums.PlayNum playNum;

    private Enums.AgeLimit ageLimit;

    private int price;

    private String designer;

    private String artwork;

    private String releaseDate;

    private Enums.Difficulty difficulty;

    private String publisher;

    private String youtubeLink;

    private int likeCount;

    private Double averageRate;

    private GameProfilePicResponseDto gameProfilePics;

    private List<Long> gameGenreIds = new ArrayList<>();
}
