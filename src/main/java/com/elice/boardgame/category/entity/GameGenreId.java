package com.elice.boardgame.category.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class GameGenreId {
    private Long game_id;
    private Long genre_id;
}
