package com.elice.boardgame.category.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;

@Data
@Embeddable
public class LikeGenreId implements Serializable {

    @JoinColumn(name = "member_id")
    private Long memberId;

    @JoinColumn(name = "genre_id")
    private Long genreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeGenreId that = (LikeGenreId) o;
        return Objects.equals(memberId, that.memberId) && Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, genreId);
    }
}
