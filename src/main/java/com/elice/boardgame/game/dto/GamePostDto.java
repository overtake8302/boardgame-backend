package com.elice.boardgame.game.dto;


import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.game.annotation.YouTubeLink;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private Enums.PlayTime playTime;

    private String releaseDate;

    @NotNull
    private Enums.PlayNum playNum;

    @NotNull
    private Enums.AgeLimit ageLimit;

    @Min(1)
    private int price;

    private String designer;

    private String artwork;

    @NotNull
    private Enums.Difficulty difficulty;

    private String publisher;

    @YouTubeLink
    private String youtubeLink;

    private List<Long> gameGenreIds = new ArrayList<>();
}
