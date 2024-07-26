package com.elice.boardgame.category.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;

@Data
@Embeddable
public class LikeGenreId implements Serializable {

    @JoinColumn(name = "user_id")
    private Long userId;

    @JoinColumn(name = "genre_id")
    private Long genreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeGenreId that = (LikeGenreId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, genreId);
    }
}
