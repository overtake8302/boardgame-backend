package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.GenreEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    Optional<GenreEntity> findByGenre(String genre);
}
