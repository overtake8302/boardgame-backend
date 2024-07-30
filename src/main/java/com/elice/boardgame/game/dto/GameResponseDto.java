package com.elice.boardgame.game.dto;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.enums.Enums;
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

    List<GameGenre> gameGenres = new ArrayList<>();
}
