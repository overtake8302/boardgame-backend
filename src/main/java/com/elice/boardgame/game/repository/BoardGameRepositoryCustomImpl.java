package com.elice.boardgame.game.repository;

import com.elice.boardgame.category.entity.QGameGenre;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.QBoardGame;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardGameRepositoryCustomImpl implements BoardGameRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardGame> findBoardGamesWithFilters(List<String> playTimes, List<String> playNums, List<String> ageLimits, List<String> prices, List<String> genres) {
        QBoardGame qBoardGame = QBoardGame.boardGame;
        QGameGenre qGameGenre = QGameGenre.gameGenre;
        BooleanBuilder builder = new BooleanBuilder();

        boolean noFilters = (playTimes == null || playTimes.isEmpty()) &&
            (playNums == null || playNums.isEmpty()) &&
            (ageLimits == null || ageLimits.isEmpty()) &&
            (prices == null || prices.isEmpty()) &&
            (genres == null || genres.isEmpty());

        if (!noFilters) {
            if (playTimes != null && !playTimes.isEmpty()) {
                BooleanExpression playTimeExpression = qBoardGame.playTime.stringValue().in(playTimes);
                builder.and(playTimeExpression);
            }
            if (playNums != null && !playNums.isEmpty()) {
                BooleanExpression playNumExpression = qBoardGame.playNum.stringValue().in(playNums);
                builder.and(playNumExpression);
            }
            if (ageLimits != null && !ageLimits.isEmpty()) {
                BooleanExpression ageLimitExpression = qBoardGame.ageLimit.stringValue().in(ageLimits);
                builder.and(ageLimitExpression);
            }
            if (prices != null && !prices.isEmpty()) {
                BooleanBuilder priceBuilder = new BooleanBuilder();
                for (String price : prices) {
                    switch (price) {
                        case "3만원 이하":
                            priceBuilder.or(qBoardGame.price.loe(30000));
                            break;
                        case "3~5만원":
                            priceBuilder.or(qBoardGame.price.between(30001, 50000));
                            break;
                        case "5~10만원":
                            priceBuilder.or(qBoardGame.price.between(50001, 100000));
                            break;
                        case "10만원 이상":
                            priceBuilder.or(qBoardGame.price.goe(100001));
                            break;
                    }
                }
                builder.and(priceBuilder);
            }
            if (genres != null && !genres.isEmpty()) {
                builder.and(qBoardGame.gameId.in(
                    queryFactory.select(qGameGenre.id.gameId)
                        .from(qGameGenre)
                        .where(qGameGenre.genre.genre.in(genres))
                ));
            }
        }

        return queryFactory.selectFrom(qBoardGame)
            .where(builder)
            .fetch();
    }

}
