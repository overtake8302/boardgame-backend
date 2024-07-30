package com.elice.boardgame.category.repository;


import com.elice.boardgame.category.entity.LikeGenre;
import java.util.List;

public interface CustomLikeGenreRepository {
    List<LikeGenre> findByUserIdOrderByScoreDesc(Long userId);
}
