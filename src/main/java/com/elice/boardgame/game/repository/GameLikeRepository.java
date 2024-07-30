package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.GameLike;
import com.elice.boardgame.game.entity.GameLikePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameLikeRepository extends JpaRepository<GameLike, GameLikePK>, CustomGameLikeRepository {

    boolean existsByGameLikePK(GameLikePK gameLikePK);
    int countLikesByBoardGameGameId(Long gameId);
}
