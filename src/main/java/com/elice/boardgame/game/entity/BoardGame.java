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

    private String name;

    @Enumerated(EnumType.STRING)
    private PlayTime playTime;

    @Enumerated(EnumType.STRING)
    private PlayNum playNum;

    @Enumerated(EnumType.STRING)
    private AgeLimit ageLimit;

    private int price;

    private String designer;

    private String artwork;

    private String releaseDate;

    @Enumerated(EnumType.STRING)
    private difficulty difficulty;

    private String publisher;

    private String youtubeLink;

    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameLike> gameLikes = new ArrayList<>();

    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameRate> gameRates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "game_id")
    private List<GameProfilePic> gameProfilePics = new ArrayList<>();

    //장르 추가하기

    @AllArgsConstructor
    @Getter
    public enum AgeLimit {

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

    @AllArgsConstructor
    @Getter
    public enum PlayNum {
        ONE_PLAYER("1인용"),
        TWO_PLAYERS("2인용"),
        THREE_PLAYERS("3인용"),
        FOUR_PLAYERS("4인용"),
        FIVE_PLUS_PLAYERS("5인 이상");

        private final String label;
    }

    @AllArgsConstructor
    @Getter
    public enum PlayTime {
        SHORT("30분 이하"),
        MEDIUM("30분 ~ 1시간"),
        LONG("1시간 이상");

        private final String label;
    }

}
