package com.elice.boardgame.category.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import java.util.Objects;
import lombok.Data;

@Embeddable
@Data
public class GameGenreId {

    @JoinColumn(name = "game_id")
    private Long gameId;

    @JoinColumn(name = "genre_id")
    private Long genreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameGenreId that = (GameGenreId) o;
        return Objects.equals(gameId, that.gameId) && Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, genreId);
    }
}
