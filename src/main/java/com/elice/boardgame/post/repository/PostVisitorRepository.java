package com.elice.boardgame.post.repository;

import com.elice.boardgame.game.entity.GameVisitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostVisitorRepository extends JpaRepository<GameVisitor, Long>, CustomPostVisitorRepository {
}
