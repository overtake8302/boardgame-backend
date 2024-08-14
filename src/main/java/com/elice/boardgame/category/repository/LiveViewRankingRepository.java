package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.LiveViewRanking;
import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiveViewRankingRepository extends JpaRepository<LiveViewRanking, Long>, CustomLiveViewRankingRepository {
}
