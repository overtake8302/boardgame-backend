package com.elice.boardgame.game.dto;

import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.game.annotation.YouTubeLink;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GamePutDto {

    @NotNull
    private Long gameId;

    @NotBlank
    private String name;

    @NotNull
    private Enums.PlayTime playTime;

    @NotNull
    private Enums.PlayNum playNum;

    @NotNull
    private Enums.AgeLimit ageLimit;

    @NotNull
    private int price;

    private String designer;

    private String artwork;

    private String releaseDate;

    @NotNull
    private Enums.Difficulty difficulty;

    private String publisher;

    @YouTubeLink
    private String youtubeLink;

    /*private int likeCount;

    private Double averageRate;*/

    private List<Long> gameGenreIds = new ArrayList<>();
}
