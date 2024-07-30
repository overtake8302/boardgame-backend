package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameLike;
import com.elice.boardgame.game.entity.GameLikePK;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameLikeRepository extends JpaRepository<GameLike, GameLikePK>, CustomGameLikeRepository {
}
