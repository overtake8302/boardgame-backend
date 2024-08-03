package com.elice.boardgame.game.repository;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.category.DTO.BoardGameRateDto;
import com.elice.boardgame.category.DTO.RatingCountDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameRate;
import com.elice.boardgame.game.entity.QBoardGame;
import com.elice.boardgame.game.entity.QGameRate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

import static com.elice.boardgame.game.entity.QBoardGame.boardGame;
import static com.elice.boardgame.game.entity.QGameRate.gameRate;

@Repository
public class CustomGameRateRepositoryImpl implements CustomGameRateRepository{

    private final JPAQueryFactory queryFactory;

    public CustomGameRateRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Double findAverageRateByBoardGame(BoardGame game) {
        return queryFactory
                .select(gameRate.rate.avg())
                .from(gameRate)
                .where(gameRate.boardGame.gameId.eq(game.getGameId()))
                .fetchOne();
    }

    @Override
    public List<RatingCountDto> countRatingsByUserId(Long userId) {
        return queryFactory.select(
                Projections.fields(RatingCountDto.class,
                    gameRate.rate.as("rate"),
                    gameRate.rate.count().as("count")))
            .from(gameRate)
            .where(gameRate.user.id.eq(userId))
            .groupBy(gameRate.rate)
            .fetch();
    }

    @Override
    public List<BoardGameRateDto> findByUserIdAndRate(Long userId, Double rate) {
        return queryFactory
            .select(Projections.fields(BoardGameRateDto.class,
                boardGame,
                gameRate.rate))
            .from(gameRate)
            .join(gameRate.boardGame, boardGame)
            .where(gameRate.user.id.eq(userId)
                .and(gameRate.rate.eq(rate)))
            .fetch();
    }

    @Override
    public List<BoardGame> findByUserId(Long userId) {
        QGameRate gameRate = QGameRate.gameRate;
        QBoardGame boardGame = QBoardGame.boardGame;

        return queryFactory
            .select(boardGame)
            .from(gameRate)
            .join(gameRate.boardGame, boardGame)
            .where(gameRate.user.id.eq(userId))
            .fetch();
    }


    @Override
    public Optional<GameRate> findByUserAndBoardGame(User user, BoardGame boardGame) {
        QGameRate gameRate = QGameRate.gameRate;
        GameRate result = queryFactory.selectFrom(gameRate)
            .where(gameRate.user.eq(user)
                .and(gameRate.boardGame.eq(boardGame)))
            .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public void deleteByUserAndBoardGame(User user, BoardGame boardGame) {
        QGameRate qGameRate = QGameRate.gameRate;

        queryFactory
            .delete(qGameRate)
            .where(qGameRate.user.eq(user)
                .and(qGameRate.boardGame.eq(boardGame)))
            .execute();
    }
}
