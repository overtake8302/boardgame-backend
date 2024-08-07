package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.QGameProfilePic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomGameProfilePicRepositoryImpl implements CustomGameProfilePicRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByGameId(Long gameId) {

        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;

        queryFactory.delete(gameProfilePic)
                .where(gameProfilePic.boardGame.gameId.eq(gameId))
                .execute();
    }
}
