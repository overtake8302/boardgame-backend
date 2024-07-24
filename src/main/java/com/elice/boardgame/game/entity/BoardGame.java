package com.elice.boardgame.game.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardGame extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    private String playNum;

    @Enumerated(EnumType.STRING)
    private ageLimit ageLimit;

    private int price;

    private String designer;

    private String artwork;

    @Enumerated(EnumType.STRING)
    private difficulty difficulty;

    private String publisher;

    private String youtubeLink;

    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameLike> gameLikes = new ArrayList<>();

    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameRate> gameRates = new ArrayList<>();

    //장르 추가하기

    @AllArgsConstructor
    @Getter
    public enum ageLimit {

        AGE_ALL("전체 이용가"),
        AGE_12_PLUS("12세 이용가"),
        AGE_15_PLUS("15세 이용가"),
        AGE_18_PLUS("청소년 이용 불가");

        private final String label;
    }

    @AllArgsConstructor
    @Getter
    public enum difficulty {

        EASY("쉬움"),
        MEDIUM("보통"),
        HARD("어려움");

        private final String label;
    }
}
