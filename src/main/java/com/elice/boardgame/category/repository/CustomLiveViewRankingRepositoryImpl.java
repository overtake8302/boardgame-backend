package com.elice.boardgame.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import com.elice.boardgame.category.entity.LiveViewRanking;
import com.elice.boardgame.category.entity.QLiveViewRanking;
import com.elice.boardgame.game.entity.BoardGame;

@RequiredArgsConstructor
@Repository
public class CustomLiveViewRankingRepositoryImpl implements CustomLiveViewRankingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<LiveViewRanking> findByGame(BoardGame game) {
        QLiveViewRanking liveViewRanking = QLiveViewRanking.liveViewRanking;
        LiveViewRanking result = jpaQueryFactory.selectFrom(liveViewRanking)
            .where(liveViewRanking.game.eq(game))
            .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<LiveViewRanking> findAllByOrderBySumScoreDesc() {
        QLiveViewRanking liveViewRanking = QLiveViewRanking.liveViewRanking;
        return jpaQueryFactory.selectFrom(liveViewRanking)
            .orderBy(liveViewRanking.sumScore.desc())
            .limit(10)
            .fetch();
    }
}
