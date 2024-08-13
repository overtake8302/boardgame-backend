package com.elice.boardgame.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "game_genre_history")
@Getter
@Setter
public class GameGenreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_history_id")
    private Long genreHistoryId;

    @Column(name = "genre_id")
    private Long genreId;

    @Column(nullable = false)
    private String genre;

    @ManyToOne
    @JoinColumn(name = "game_history_id")
    private BoardGameHistory gameHistory;
}
