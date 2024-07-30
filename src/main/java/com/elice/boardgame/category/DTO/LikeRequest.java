package com.elice.boardgame.category.DTO;

import com.elice.boardgame.category.entity.LikeGenreId;
import lombok.Data;

@Data
public class LikeRequest {

    private LikeGenreId id;
    private Boolean like;
}
