package com.elice.boardgame.game.repository;

import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.game.dto.GameHistoriesResponseDto;
import com.elice.boardgame.game.dto.GameHistoryResponseDto;
import com.elice.boardgame.game.entity.GameGenreHistory;
import com.elice.boardgame.game.entity.QBoardGameHistory;
import com.elice.boardgame.game.entity.QGameGenreHistory;
import com.elice.boardgame.game.entity.QGameProfilePic;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
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
public class CustomBoardGameHistoryRepositoryImpl implements CustomBoardGameHistoryRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public Page<GameHistoriesResponseDto> findHistoriesByGameId(Pageable pageable, Long gameId) {

        QBoardGameHistory gameHistory = QBoardGameHistory.boardGameHistory;

        List<GameHistoriesResponseDto> results = queryFactory
                .select(Projections.bean(GameHistoriesResponseDto.class,
                        gameHistory.boardGame.gameId.as("gameId"),
                        gameHistory.boardGame.name.as("name"),
                        gameHistory.createdAt.as("createdAt"),
                        gameHistory.gameHistoryId.as("gameHistoryId"),
                        gameHistory.creator.username.as("editBy")))
                .from(gameHistory)
                .where(gameHistory.boardGame.gameId.eq(gameId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(gameHistory.gameHistoryId.desc())
                .fetch();

        long total = queryFactory
                .select(gameHistory.count())
                .from(gameHistory)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public GameHistoryResponseDto findHistoryByHistoryId(Long historyId) {

        QBoardGameHistory gameHistory = QBoardGameHistory.boardGameHistory;
        QGameGenreHistory genreHistory = QGameGenreHistory.gameGenreHistory;
        QGameProfilePic gameProfilePic = QGameProfilePic.gameProfilePic;

        GameHistoryResponseDto result = queryFactory
                .select(Projections.bean(GameHistoryResponseDto.class,
                        gameHistory.gameHistoryId.as("gameHistoryId"),
                        gameHistory.boardGame.gameId.as("gameId"),
                        gameHistory.name.as("name"),
                        new CaseBuilder()
                                .when(gameHistory.playTime.eq(Enums.PlayTime.SHORT)).then("30분 이하")
                                .when(gameHistory.playTime.eq(Enums.PlayTime.MEDIUM)).then("30분 ~ 1시간")
                                .when(gameHistory.playTime.eq(Enums.PlayTime.LONG)).then("1시간 이상")
                                .otherwise(gameHistory.playTime.stringValue()).as("playTime"),
                        new CaseBuilder()
                                .when(gameHistory.playNum.eq(Enums.PlayNum.ONE_PLAYER)).then("1인용")
                                .when(gameHistory.playNum.eq(Enums.PlayNum.TWO_PLAYERS)).then("2인용")
                                .when(gameHistory.playNum.eq(Enums.PlayNum.THREE_PLAYERS)).then("3인용")
                                .when(gameHistory.playNum.eq(Enums.PlayNum.FOUR_PLAYERS)).then("4인용")
                                .when(gameHistory.playNum.eq(Enums.PlayNum.FIVE_PLUS_PLAYERS)).then("5인 이상")
                                .otherwise(gameHistory.playNum.stringValue()).as("playNum"),
                        new CaseBuilder()
                                .when(gameHistory.ageLimit.eq(Enums.AgeLimit.AGE_ALL)).then("전체 이용가")
                                .when(gameHistory.ageLimit.eq(Enums.AgeLimit.AGE_12_PLUS)).then("12세 이용가")
                                .when(gameHistory.ageLimit.eq(Enums.AgeLimit.AGE_15_PLUS)).then("15세 이용가")
                                .when(gameHistory.ageLimit.eq(Enums.AgeLimit.AGE_18_PLUS)).then("청소년 이용 불가")
                                .otherwise(gameHistory.ageLimit.stringValue()).as("ageLimit"),
                        new CaseBuilder()
                                .when(gameHistory.difficulty.eq(Enums.Difficulty.EASY)).then("쉬움")
                                .when(gameHistory.difficulty.eq(Enums.Difficulty.MEDIUM)).then("보통")
                                .when(gameHistory.difficulty.eq(Enums.Difficulty.HARD)).then("어려움")
                                .otherwise(gameHistory.difficulty.stringValue()).as("difficulty"),
                        gameHistory.price.as("price"),
                        gameHistory.designer.as("designer"),
                        gameHistory.artwork.as("artwork"),
                        gameHistory.releaseDate.as("releaseDate"),
                        gameHistory.publisher.as("publisher"),
                        gameHistory.youtubeLink.as("youtubeLink"),
                        gameHistory.createdAt.as("createdAt"),
                        gameHistory.creator.username.as("creatorName")
                        ))
                .from(gameHistory)
                .where(gameHistory.gameHistoryId.eq(historyId))
                .fetchOne();

        List<String> pics = queryFactory
                .select(gameProfilePic.picAddress)
                .from(gameProfilePic)
                .where(gameProfilePic.gameHistory.gameHistoryId.eq(historyId))
                .fetch();

        List<GameGenreHistory> genreHistories = queryFactory
                .select(Projections.bean(GameGenreHistory.class,
                        genreHistory.genreId.as("genreId"),
                        genreHistory.genre.as("genre")
                        ))
                .from(genreHistory)
                .where(genreHistory.gameHistory.gameHistoryId.eq(historyId))
                .fetch();

        if (result != null) {
            result.setGameProfilePics(pics);
            result.setGameGenres(genreHistories);
        }

        return result;
    }
}
