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

@Entity(name = "board_game_history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardGameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_history_id")
    private Long gameHistoryId;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

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

    @OneToMany(mappedBy = "gameHistory")
    private List<GameProfilePic> gameProfilePics = new ArrayList<>();

    @OneToMany(mappedBy = "gameHistory")
    private List<GameGenreHistory> gameGenres;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
