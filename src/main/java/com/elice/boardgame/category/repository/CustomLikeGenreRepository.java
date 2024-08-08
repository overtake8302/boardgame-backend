package com.elice.boardgame.category.repository;


import com.elice.boardgame.category.entity.LikeGenre;
import java.util.List;
import java.util.Optional;

public interface CustomLikeGenreRepository {
    List<LikeGenre> findByUserIdOrderByScoreDesc(Long userId);
    Optional<LikeGenre> findByLikeGenreId(Long userId, Long genreId);
}
