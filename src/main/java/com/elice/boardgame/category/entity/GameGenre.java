package com.elice.boardgame.category.entity;

import com.elice.boardgame.game.entity.BoardGame;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;

@Entity
@Table(name = "GameGenre")
@Data
public class GameGenre implements Serializable {

    @EmbeddedId
    private GameGenreId id;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameGenre that = (GameGenre) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
