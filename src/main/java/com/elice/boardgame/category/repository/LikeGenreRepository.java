package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.LikeGenre;
import com.elice.boardgame.category.entity.LikeGenreId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeGenreRepository extends JpaRepository<LikeGenre, LikeGenreId> {
    List<LikeGenre> findByUserIdOrderByScoreDesc(Long userId);
}