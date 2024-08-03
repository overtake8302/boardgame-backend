package com.elice.boardgame.game.repository;

import com.elice.boardgame.category.entity.QGameGenre;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.QBoardGame;
import com.elice.boardgame.game.entity.QGameLike;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomBoardGameRepositoryImpl implements CustomBoardGameRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardGame> findBoardGamesWithFilters(List<String> playTimes, List<String> playNums,
        List<String> ageLimits, List<String> prices, List<String> genres) {
        QBoardGame qBoardGame = QBoardGame.boardGame;
        QGameGenre qGameGenre = QGameGenre.gameGenre;
        BooleanBuilder builder = new BooleanBuilder();

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

        return queryFactory.selectFrom(qBoardGame)
            .where(builder)
            .fetch();
    }

    @Override
    public List<BoardGame> findByGenres(List<Long> genreIds, Long userId) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameGenre gameGenre = QGameGenre.gameGenre;
        QGameLike gameLike = QGameLike.gameLike;

        // 각 genreId를 확인하는 조건 추가
        BooleanExpression predicate = gameGenre.genre.genreId.in(genreIds);

        // 좋아요를 누른 게임 ID 가져오기
        List<Long> likedGameIds = queryFactory.select(gameLike.boardGame.gameId)
            .from(gameLike)
            .where(gameLike.user.id.eq(userId))
            .fetch();

        // game_id를 그룹화하고 count(genre_id)로 정렬
        List<Long> boardGameIds = queryFactory
            .select(gameGenre.boardGame.gameId)
            .from(gameGenre)
            .where(predicate)
            .groupBy(gameGenre.boardGame.gameId)
            .orderBy(gameGenre.genre.genreId.count().desc())
            .fetch();

        // 보드게임 ID로 보드게임을 조회하며 좋아요를 누른 게임은 제외
        return queryFactory
            .selectFrom(boardGame)
            .where(boardGame.gameId.in(boardGameIds) //서브쿼리 사용해서 좋아요 누른 게임 제외
                .and(boardGame.gameId.notIn(likedGameIds)))
            .fetch();
    }

}
