package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.Genre;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameGenreRepository extends JpaRepository<GameGenre, Long>, CustomGameGenreRepository {
}
