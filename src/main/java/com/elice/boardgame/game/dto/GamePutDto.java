package com.elice.boardgame.game.dto;

import com.elice.boardgame.game.entity.BoardGame;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GamePutDto {

    private Long gameId;

    private String name;

    private BoardGame.PlayTime playTime;

    private BoardGame.PlayNum playNum;

    private BoardGame.AgeLimit ageLimit;

    private int price;

    private String designer;

    private String artwork;

    private String releaseDate;

    private BoardGame.difficulty difficulty;

    private String publisher;

    private String youtubeLink;

    private int likeCount;

    private Double averageRate;

    private GameProfilePicResponseDto gameProfilePics;
}
