package com.elice.boardgame.category.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Game_Genre")
@Data
public class GameGenreEntity {

    @EmbeddedId
    private GameGenreId id;

    @ManyToOne
    @MapsId("game_id")
    @JoinColumn(name = "game_id")
    private BoardGameEntity boardGame;

    @ManyToOne
    @MapsId("genre_id")
    @JoinColumn(name = "genre_id")
    private GenreEntity genre;
}
