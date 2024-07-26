package com.elice.boardgame.category.entity;

import com.elice.boardgame.auth.entity.User;
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
public class LikeGenre implements Serializable {

    @EmbeddedId
    private LikeGenreId id;

    @Column(nullable = false)
    private Long score;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id", insertable = false, updatable = false)
    private Genre genre;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LikeGenre likeGenre = (LikeGenre) o;
        return Objects.equals(id, likeGenre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
