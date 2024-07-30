package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.QBoardGame;
import com.elice.boardgame.game.entity.QGameLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomGameLikeRepositoryImpl implements CustomGameLikeRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardGame> findLikedGamesByUserId(Long userId) {
        QGameLike gameLike = QGameLike.gameLike;
        QBoardGame boardGame = QBoardGame.boardGame;

        return queryFactory
            .select(boardGame)
            .from(gameLike)
            .join(gameLike.boardGame, boardGame)
            .where(gameLike.gameLikePK.userId.eq(userId))
            .orderBy(gameLike.createdDate.desc())
            .fetch();
    }
}
