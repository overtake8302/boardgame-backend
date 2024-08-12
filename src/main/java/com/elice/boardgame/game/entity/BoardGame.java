package com.elice.boardgame.game.entity;


import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.common.entity.BaseEntity;
import com.elice.boardgame.common.enums.Enums;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "board_game")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardGame extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameId;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "play_time")
    private Enums.PlayTime playTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "play_num")
    private Enums.PlayNum playNum;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_limit")
    private Enums.AgeLimit ageLimit;

    private int price;

    private String designer;

    private String artwork;

    @Column(name = "release_date")
    private String releaseDate;

    @Enumerated(EnumType.STRING)
    private Enums.Difficulty difficulty;

    private String publisher;

    @Column(length = 500, name = "youtube_link")
    private String youtubeLink;

    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameLike> gameLikes = new ArrayList<>();

    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameRate> gameRates = new ArrayList<>();

    @OneToMany(mappedBy = "boardGame")
    private List<GameProfilePic> gameProfilePics = new ArrayList<>();

    //장르 추가하기
    @OneToMany
    @JoinColumn(name = "game_genre_id")
    @JsonManagedReference
    private List<GameGenre> gameGenres;

    @OneToMany
    @JoinColumn(name = "game_id")
    private List<GameVisitor> gameVisitors;

    private Long views;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User firstCreator;
}
