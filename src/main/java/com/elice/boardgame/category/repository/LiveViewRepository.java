package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.LiveViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveViewRepository extends JpaRepository<LiveViewEntity, Long> {
}
