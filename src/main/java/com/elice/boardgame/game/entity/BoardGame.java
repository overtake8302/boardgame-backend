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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private Enums.Difficulty difficulty;

    private String publisher;

    @Column(length = 500, name = "youtube_link")
    private String youtubeLink;

    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GameLike> gameLikes = new ArrayList<>();

    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GameRate> gameRates = new ArrayList<>();

    @OneToMany(mappedBy = "boardGame")
    private List<GameProfilePic> gameProfilePics = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "game_genre_id")
    @JsonManagedReference
    private List<GameGenre> gameGenres;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private List<GameVisitor> gameVisitors;

    private Long views;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User firstCreator;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id")
    private User editBy;

    @OneToMany(mappedBy = "boardGame")
    private List<BoardGameHistory> boardGameHistory;
}
