package com.elice.boardgame.category.DTO;

import com.elice.boardgame.category.entity.LikeGenreId;
import lombok.Data;

@Data
public class RatingRequest {
    private LikeGenreId id;
    private int rating;
    private Boolean check;
}
