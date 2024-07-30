package com.elice.boardgame.game.dto;


import com.elice.boardgame.enumeration.AgeLimit;
import com.elice.boardgame.enumeration.Difficulty;
import com.elice.boardgame.enumeration.PlayNum;
import com.elice.boardgame.enumeration.PlayTime;
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

    private PlayTime playTime;

    private String releaseDate;

    @NotNull
    private PlayNum playNum;

    @NotNull
    private AgeLimit ageLimit;

    @Min(1)
    private int price;

    private String designer;

    private String artwork;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private String publisher;

    private String youtubeLink;

    private List<Long> gameGenreIds = new ArrayList<>();
}
