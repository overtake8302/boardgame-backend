package com.elice.boardgame.game.dto;


import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.game.annotation.YouTubeLink;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GamePostDto {

    @NotBlank
    private String name;

    private Enums.PlayTime playTime;

    private LocalDate releaseDate;

    @NotNull
    private Enums.PlayNum playNum;

    @NotNull
    private Enums.AgeLimit ageLimit;

    @Max(2100000000)
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
