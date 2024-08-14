package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.category.entity.Genre;
import com.elice.boardgame.category.entity.QGameGenre;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.QBoardGame;
import com.elice.boardgame.game.entity.QGameLike;
import com.elice.boardgame.game.entity.QGameRate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CustomGameGenreRepositoryImpl implements CustomGameGenreRepository{
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<BoardGame> findBoardGamesByGenresOrderByRatingDesc(List<Genre> genres, Long userId, Pageable pageable) {
        QGameGenre gameGenre = QGameGenre.gameGenre;
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameRate gameRate = QGameRate.gameRate;
        QGameLike gameLike = QGameLike.gameLike;

        List<Long> likedGameIds = jpaQueryFactory.select(gameLike.boardGame.gameId)
            .from(gameLike)
            .where(gameLike.user.id.eq(userId))
            .fetch();

        List<BoardGame> boardGames = jpaQueryFactory.select(boardGame)
            .from(gameGenre)
            .join(gameGenre.boardGame, boardGame)
            .join(boardGame.gameRates, gameRate)
            .where(gameGenre.genre.in(genres)
                .and(boardGame.deletedAt.isNull())
                .and(boardGame.gameId.notIn(likedGameIds)))
            .groupBy(boardGame)
            .orderBy(gameRate.rate.avg().desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = jpaQueryFactory
            .select(boardGame.countDistinct())
            .from(gameGenre)
            .join(gameGenre.boardGame, boardGame)
            .where(gameGenre.genre.in(genres)
                .and(boardGame.deletedAt.isNull())
                .and(boardGame.gameId.notIn(likedGameIds)))
            .fetchOne();

        return new PageImpl<>(boardGames, pageable, total);
    }

    @Override
    public Optional<List<Genre>> findGenresByBoardGame(BoardGame boardGame) {
        QGameGenre gameGenre = QGameGenre.gameGenre;

        List<Genre> genres = jpaQueryFactory.select(gameGenre.genre)
            .from(gameGenre)
            .where(gameGenre.boardGame.eq(boardGame))
            .fetch();

        return Optional.ofNullable(genres);
    }
}
