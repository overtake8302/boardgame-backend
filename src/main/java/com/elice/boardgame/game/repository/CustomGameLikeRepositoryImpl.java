package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.QBoardGame;
import com.elice.boardgame.game.entity.QGameLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomGameLikeRepositoryImpl implements CustomGameLikeRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardGame> findLikedGamesByUserId(Long userId, Pageable pageable) {
        QGameLike gameLike = QGameLike.gameLike;
        QBoardGame boardGame = QBoardGame.boardGame;

        List<BoardGame> boardGames = queryFactory
            .select(boardGame)
            .from(gameLike)
            .join(gameLike.boardGame, boardGame)
            .where(gameLike.gameLikePK.userId.eq(userId))
            .orderBy(gameLike.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(boardGame.count())
            .from(gameLike)
            .where(gameLike.gameLikePK.userId.eq(userId))
            .fetchOne();

        return new PageImpl<>(boardGames, pageable, total);
    }
}
