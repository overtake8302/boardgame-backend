package com.elice.boardgame.game.dto;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameLike;
import com.elice.boardgame.game.entity.GameProfilePic;
import com.elice.boardgame.game.entity.GameRate;
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
