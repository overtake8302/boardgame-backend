package com.elice.boardgame.game.dto;


import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameProfilePic;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GamePostDto {

    @NotBlank
    private String name;

//    private List<GameProfilePic> gameProfilePic;

    //장르 추가하기

    private BoardGame.PlayTime playTime;

    private String releaseDate;

    @NotNull
    private BoardGame.PlayNum playNum;

    @NotNull
    private BoardGame.AgeLimit ageLimit;

    @Min(1)
    private int price;

    private String designer;

    private String artwork;

    @Enumerated(EnumType.STRING)
    private BoardGame.difficulty difficulty;

    private String publisher;

    private String youtubeLink;

    private List<Long> gameGenreIds = new ArrayList<>();
}
