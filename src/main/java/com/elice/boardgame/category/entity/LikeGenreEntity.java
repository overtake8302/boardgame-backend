package com.elice.boardgame.category.entity;

import jakarta.persistence.Column;
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
@Table(name = "Like_Genre")
@Data
public class LikeGenreEntity implements Serializable {

    @EmbeddedId
    private LikeGenreId id;

    @Column(nullable = false)
    private Long score;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id", insertable = false, updatable = false)
    private GenreEntity genre;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LikeGenreEntity likeGenre = (LikeGenreEntity) o;
        return Objects.equals(id, likeGenre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
