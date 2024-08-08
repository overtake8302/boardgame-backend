package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.LiveView;
import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Bool;

@Repository
public interface LiveViewRepository extends JpaRepository<LiveView, Long> {
    Optional<LiveView> findByGame(BoardGame game);
}
