package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.BoardGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardGameRepository extends JpaRepository<BoardGameEntity, Long> {
}
