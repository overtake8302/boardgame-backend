package com.elice.boardgame.game.repository;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.QGameGenre;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomBoardGameRepositoryImpl implements CustomBoardGameRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

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

    @Override
    public GameResponseDto getGameResponseDtoByGameIdAndDeletedDateIsNull(Long gameId) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameRate gameRate = QGameRate.gameRate;
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;
        QGameGenre gameGenre = QGameGenre.gameGenre;

        List<String> profilePics = queryFactory
                .select(gameProfilePic.picAddress)
                .from(gameProfilePic)
                .where(gameProfilePic.boardGame.gameId.eq(gameId))
                .fetch();

        List<GameGenre> genres = queryFactory
                .select(gameGenre)
                .from(gameGenre)
                .where(gameGenre.boardGame.gameId.eq(gameId))
                .fetch();

        GameResponseDto result = queryFactory
                .select(Projections.bean(GameResponseDto.class,
                        boardGame.gameId.as("gameId"),
                        boardGame.name.as("name"),
                        new CaseBuilder()
                                .when(boardGame.playTime.eq(Enums.PlayTime.SHORT)).then("30분 이하")
                                .when(boardGame.playTime.eq(Enums.PlayTime.MEDIUM)).then("30분 ~ 1시간")
                                .when(boardGame.playTime.eq(Enums.PlayTime.LONG)).then("1시간 이상")
                                .otherwise(boardGame.playTime.stringValue()).as("playTime"),
                        new CaseBuilder()
                                .when(boardGame.playNum.eq(Enums.PlayNum.ONE_PLAYER)).then("1인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.TWO_PLAYERS)).then("2인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.THREE_PLAYERS)).then("3인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.FOUR_PLAYERS)).then("4인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.FIVE_PLUS_PLAYERS)).then("5인 이상")
                                .otherwise(boardGame.playNum.stringValue()).as("playNum"),
                        new CaseBuilder()
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_ALL)).then("전체 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_12_PLUS)).then("12세 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_15_PLUS)).then("15세 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_18_PLUS)).then("청소년 이용 불가")
                                .otherwise(boardGame.ageLimit.stringValue()).as("ageLimit"),
                        new CaseBuilder()
                                .when(boardGame.difficulty.eq(Enums.Difficulty.EASY)).then("쉬움")
                                .when(boardGame.difficulty.eq(Enums.Difficulty.MEDIUM)).then("보통")
                                .when(boardGame.difficulty.eq(Enums.Difficulty.HARD)).then("어려움")
                                .otherwise(boardGame.difficulty.stringValue()).as("difficulty"),
                        boardGame.price.as("price"),
                        boardGame.designer.as("designer"),
                        boardGame.artwork.as("artwork"),
                        boardGame.releaseDate.as("releaseDate"),
                        boardGame.publisher.as("publisher"),
                        boardGame.youtubeLink.as("youtubeLink"),
                        boardGame.gameLikes.size().as("likeCount"),
                        gameRate.rate.avg().as("averageRate"),
                        gameVisitor.id.count().as("views")
                ))
                .from(boardGame)
                .leftJoin(boardGame.gameRates, gameRate)
                .leftJoin(boardGame.gameVisitors, gameVisitor)
                .leftJoin(boardGame.gameProfilePics, gameProfilePic) // 프로필 사진 조인
                .leftJoin(boardGame.gameGenres, gameGenre) // 장르 조인
                .where(boardGame.deletedDate.isNull()
                        .and(boardGame.gameId.eq(gameId)))
                .groupBy(boardGame.gameId)
                .orderBy(gameRate.rate.avg().desc())
                .fetchOne();

        if (result != null) {
            result.setGameProfilePics(profilePics);
            result.setGameGenres(genres);
        }

        return result;
    }

    @Override
    public List<GameResponseDto> findByNameContainingAndDeletedDateIsNull(String keyword) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QGameRate gameRate = QGameRate.gameRate;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;
        QGameGenre gameGenre = QGameGenre.gameGenre;

        List<GameResponseDto> results = queryFactory
                .select(Projections.bean(GameResponseDto.class,
                        boardGame.gameId.as("gameId"),
                        boardGame.name.as("name"),
                        new CaseBuilder()
                                .when(boardGame.playTime.eq(Enums.PlayTime.SHORT)).then("30분 이하")
                                .when(boardGame.playTime.eq(Enums.PlayTime.MEDIUM)).then("30분 ~ 1시간")
                                .when(boardGame.playTime.eq(Enums.PlayTime.LONG)).then("1시간 이상")
                                .otherwise(boardGame.playTime.stringValue()).as("playTime"),
                        new CaseBuilder()
                                .when(boardGame.playNum.eq(Enums.PlayNum.ONE_PLAYER)).then("1인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.TWO_PLAYERS)).then("2인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.THREE_PLAYERS)).then("3인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.FOUR_PLAYERS)).then("4인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.FIVE_PLUS_PLAYERS)).then("5인 이상")
                                .otherwise(boardGame.playNum.stringValue()).as("playNum"),
                        new CaseBuilder()
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_ALL)).then("전체 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_12_PLUS)).then("12세 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_15_PLUS)).then("15세 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_18_PLUS)).then("청소년 이용 불가")
                                .otherwise(boardGame.ageLimit.stringValue()).as("ageLimit"),
                        new CaseBuilder()
                                .when(boardGame.difficulty.eq(Enums.Difficulty.EASY)).then("쉬움")
                                .when(boardGame.difficulty.eq(Enums.Difficulty.MEDIUM)).then("보통")
                                .when(boardGame.difficulty.eq(Enums.Difficulty.HARD)).then("어려움")
                                .otherwise(boardGame.difficulty.stringValue()).as("difficulty"),
                        boardGame.price.as("price"),
                        boardGame.designer.as("designer"),
                        boardGame.artwork.as("artwork"),
                        boardGame.releaseDate.as("releaseDate"),
                        boardGame.publisher.as("publisher"),
                        boardGame.youtubeLink.as("youtubeLink"),
                        boardGame.gameLikes.size().as("likeCount"),
                        gameRate.rate.avg().as("averageRate"),
                        gameVisitor.id.count().as("views")
                ))
                .from(boardGame)
                .leftJoin(boardGame.gameRates, gameRate)
                .leftJoin(boardGame.gameVisitors, gameVisitor)
                .leftJoin(boardGame.gameProfilePics, gameProfilePic) // 프로필 사진 조인
                .leftJoin(boardGame.gameGenres, gameGenre) // 장르 조인
                .where(boardGame.name.contains(keyword).and(boardGame.deletedDate.isNull()))
                .groupBy(
                        boardGame.gameId,
                        boardGame.name,
                        boardGame.playTime,
                        boardGame.playNum,
                        boardGame.ageLimit,
                        boardGame.difficulty,
                        boardGame.price,
                        boardGame.designer,
                        boardGame.artwork,
                        boardGame.releaseDate,
                        boardGame.publisher,
                        boardGame.youtubeLink,
                        gameRate.rate,
                        gameVisitor.id
                )
                .fetch();

        for (GameResponseDto result : results) {
            List<String> profilePics = queryFactory
                    .select(gameProfilePic.picAddress)
                    .from(gameProfilePic)
                    .where(gameProfilePic.boardGame.gameId.eq(result.getGameId()))
                    .fetch();
            result.setGameProfilePics(profilePics);

            List<GameGenre> genres = queryFactory
                    .select(gameGenre)
                    .from(gameGenre)
                    .where(gameGenre.boardGame.gameId.eq(result.getGameId()))
                    .fetch();
            result.setGameGenres(genres);
        }

        return results;
    }

    public Page<GameResponseDto> findAllByDeletedDateIsNull(Pageable pageable, Enums.GameListSortOption sortBy) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QGameRate gameRate = QGameRate.gameRate;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;
        QGameGenre gameGenre = QGameGenre.gameGenre;

        JPAQuery<GameResponseDto> query = queryFactory
                .select(Projections.bean(GameResponseDto.class,
                        boardGame.gameId.as("gameId"),
                        boardGame.name.as("name"),
                        new CaseBuilder()
                                .when(boardGame.playTime.eq(Enums.PlayTime.SHORT)).then("30분 이하")
                                .when(boardGame.playTime.eq(Enums.PlayTime.MEDIUM)).then("30분 ~ 1시간")
                                .when(boardGame.playTime.eq(Enums.PlayTime.LONG)).then("1시간 이상")
                                .otherwise(boardGame.playTime.stringValue()).as("playTime"),
                        new CaseBuilder()
                                .when(boardGame.playNum.eq(Enums.PlayNum.ONE_PLAYER)).then("1인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.TWO_PLAYERS)).then("2인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.THREE_PLAYERS)).then("3인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.FOUR_PLAYERS)).then("4인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.FIVE_PLUS_PLAYERS)).then("5인 이상")
                                .otherwise(boardGame.playNum.stringValue()).as("playNum"),
                        new CaseBuilder()
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_ALL)).then("전체 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_12_PLUS)).then("12세 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_15_PLUS)).then("15세 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_18_PLUS)).then("청소년 이용 불가")
                                .otherwise(boardGame.ageLimit.stringValue()).as("ageLimit"),
                        new CaseBuilder()
                                .when(boardGame.difficulty.eq(Enums.Difficulty.EASY)).then("쉬움")
                                .when(boardGame.difficulty.eq(Enums.Difficulty.MEDIUM)).then("보통")
                                .when(boardGame.difficulty.eq(Enums.Difficulty.HARD)).then("어려움")
                                .otherwise(boardGame.difficulty.stringValue()).as("difficulty"),
                        boardGame.price.as("price"),
                        boardGame.designer.as("designer"),
                        boardGame.artwork.as("artwork"),
                        boardGame.releaseDate.as("releaseDate"),
                        boardGame.publisher.as("publisher"),
                        boardGame.youtubeLink.as("youtubeLink"),
                        boardGame.gameLikes.size().as("likeCount"),
                        gameRate.rate.avg().as("averageRate"),
                        gameVisitor.id.count().as("views")
                ))
                .from(boardGame)
                .leftJoin(boardGame.gameRates, gameRate)
                .leftJoin(boardGame.gameVisitors, gameVisitor)
                .leftJoin(boardGame.gameProfilePics, gameProfilePic)
                .leftJoin(boardGame.gameGenres, gameGenre)
                .where(boardGame.deletedDate.isNull())
                .groupBy(
                        boardGame.gameId,
                        boardGame.name,
                        boardGame.playTime,
                        boardGame.playNum,
                        boardGame.ageLimit,
                        boardGame.difficulty,
                        boardGame.price,
                        boardGame.designer,
                        boardGame.artwork,
                        boardGame.releaseDate,
                        boardGame.publisher,
                        boardGame.youtubeLink,
                        gameRate.rate,
                        gameVisitor.id
                );

        if (sortBy.equals(Enums.GameListSortOption.GAME_ID)) {
            query.orderBy(boardGame.gameId.desc());
        } else if (sortBy.equals(Enums.GameListSortOption.AVERAGE_RATE)) {
            NumberExpression<Double> averageRate = gameRate.rate.avg();
            OrderSpecifier<Double> orderSpecifier = new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, averageRate);
            query.orderBy(orderSpecifier);
        } else if (sortBy.equals(Enums.GameListSortOption.DIFFICULTY)) {
            NumberExpression<Integer> difficultyOrder = new CaseBuilder()
                    .when(boardGame.difficulty.eq(Enums.Difficulty.HARD)).then(0)
                    .when(boardGame.difficulty.eq(Enums.Difficulty.MEDIUM)).then(1)
                    .when(boardGame.difficulty.eq(Enums.Difficulty.EASY)).then(2)
                    .otherwise(3);
            query.orderBy(new OrderSpecifier<>(com.querydsl.core.types.Order.ASC, difficultyOrder));
        } else if (sortBy.equals(Enums.GameListSortOption.VIEWS)) {
            query.orderBy(gameVisitor.id.count().desc());
        }

        List<GameResponseDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        for (GameResponseDto result : results) {
            List<String> profilePics = queryFactory
                    .select(gameProfilePic.picAddress)
                    .from(gameProfilePic)
                    .where(gameProfilePic.boardGame.gameId.eq(result.getGameId()))
                    .fetch();
            result.setGameProfilePics(profilePics);

            List<GameGenre> genres = queryFactory
                    .select(gameGenre)
                    .from(gameGenre)
                    .where(gameGenre.boardGame.gameId.eq(result.getGameId()))
                    .fetch();
            result.setGameGenres(genres);
        }

        long total = queryFactory
                .select(boardGame.count())
                .from(boardGame)
                .where(boardGame.deletedDate.isNull())
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<GameResponseDto> findByGameGenresGenreGenre(String genre, Pageable pageable) {
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameGenre gameGenre = QGameGenre.gameGenre;
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QGameRate gameRate = QGameRate.gameRate;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;

        List<GameResponseDto> results = queryFactory
                .select(Projections.bean(GameResponseDto.class,
                        boardGame.gameId.as("gameId"),
                        boardGame.name.as("name"),
                        new CaseBuilder()
                                .when(boardGame.playTime.eq(Enums.PlayTime.SHORT)).then("30분 이하")
                                .when(boardGame.playTime.eq(Enums.PlayTime.MEDIUM)).then("30분 ~ 1시간")
                                .when(boardGame.playTime.eq(Enums.PlayTime.LONG)).then("1시간 이상")
                                .otherwise(boardGame.playTime.stringValue()).as("playTime"),
                        new CaseBuilder()
                                .when(boardGame.playNum.eq(Enums.PlayNum.ONE_PLAYER)).then("1인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.TWO_PLAYERS)).then("2인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.THREE_PLAYERS)).then("3인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.FOUR_PLAYERS)).then("4인용")
                                .when(boardGame.playNum.eq(Enums.PlayNum.FIVE_PLUS_PLAYERS)).then("5인 이상")
                                .otherwise(boardGame.playNum.stringValue()).as("playNum"),
                        new CaseBuilder()
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_ALL)).then("전체 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_12_PLUS)).then("12세 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_15_PLUS)).then("15세 이용가")
                                .when(boardGame.ageLimit.eq(Enums.AgeLimit.AGE_18_PLUS)).then("청소년 이용 불가")
                                .otherwise(boardGame.ageLimit.stringValue()).as("ageLimit"),
                        new CaseBuilder()
                                .when(boardGame.difficulty.eq(Enums.Difficulty.EASY)).then("쉬움")
                                .when(boardGame.difficulty.eq(Enums.Difficulty.MEDIUM)).then("보통")
                                .when(boardGame.difficulty.eq(Enums.Difficulty.HARD)).then("어려움")
                                .otherwise(boardGame.difficulty.stringValue()).as("difficulty"),
                        boardGame.price.as("price"),
                        boardGame.designer.as("designer"),
                        boardGame.artwork.as("artwork"),
                        boardGame.releaseDate.as("releaseDate"),
                        boardGame.publisher.as("publisher"),
                        boardGame.youtubeLink.as("youtubeLink"),
                        boardGame.gameLikes.size().as("likeCount"),
                        gameRate.rate.avg().as("averageRate"),
                        gameVisitor.id.count().as("views")
                ))
                .from(boardGame)
                .leftJoin(boardGame.gameRates, gameRate)
                .leftJoin(boardGame.gameVisitors, gameVisitor)
                .leftJoin(boardGame.gameProfilePics, gameProfilePic) // 프로필 사진 조인
                .leftJoin(boardGame.gameGenres, gameGenre) // 장르 조인
                .where(gameGenre.genre.genre.eq(genre).and(boardGame.deletedDate.isNull()))
                .groupBy(
                        boardGame.gameId,
                        boardGame.name,
                        boardGame.playTime,
                        boardGame.playNum,
                        boardGame.ageLimit,
                        boardGame.difficulty,
                        boardGame.price,
                        boardGame.designer,
                        boardGame.artwork,
                        boardGame.releaseDate,
                        boardGame.publisher,
                        boardGame.youtubeLink,
                        gameRate.rate,
                        gameVisitor.id
                )
                .orderBy(gameRate.rate.avg().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        for (GameResponseDto result : results) {
            List<String> profilePics = queryFactory
                    .select(gameProfilePic.picAddress)
                    .from(gameProfilePic)
                    .where(gameProfilePic.boardGame.gameId.eq(result.getGameId()))
                    .fetch();
            result.setGameProfilePics(profilePics);

            List<GameGenre> genres = queryFactory
                    .select(gameGenre)
                    .from(gameGenre)
                    .where(gameGenre.boardGame.gameId.eq(result.getGameId()))
                    .fetch();
            result.setGameGenres(genres);
        }

        return results;
    }

}
