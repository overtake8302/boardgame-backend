package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.LikeGenre;
import com.elice.boardgame.category.entity.LikeGenreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LikeGenreRepository extends JpaRepository<LikeGenre, LikeGenreId> {

    @Modifying
    @Transactional
    @Query("UPDATE LikeGenre l SET l.score = l.score + :delta WHERE l.id = :id")
    void updateLikeGenreScore(LikeGenreId id, Long delta);
}