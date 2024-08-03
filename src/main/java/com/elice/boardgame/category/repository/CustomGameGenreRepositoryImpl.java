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

@RequiredArgsConstructor
public class CustomGameGenreRepositoryImpl implements CustomGameGenreRepository{
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Optional<List<BoardGame>> findBoardGamesByGenresOrderByRatingDesc(List<Genre> genres, Long userId) {
        QGameGenre gameGenre = QGameGenre.gameGenre;
        QBoardGame boardGame = QBoardGame.boardGame;
        QGameRate gameRate = QGameRate.gameRate;
        QGameLike gameLike = QGameLike.gameLike;

        // 좋아요를 누른 게임 ID 가져오기
        List<Long> likedGameIds = jpaQueryFactory.select(gameLike.boardGame.gameId)
            .from(gameLike)
            .where(gameLike.user.id.eq(userId))
            .fetch();

        List<BoardGame> boardGames = jpaQueryFactory.select(boardGame)
            .from(gameGenre)
            .join(gameGenre.boardGame, boardGame)
            .join(boardGame.gameRates, gameRate)
            .where(gameGenre.genre.in(genres)
                .and(boardGame.gameId.notIn(likedGameIds)))
            .groupBy(boardGame)
            .orderBy(gameRate.rate.avg().desc())
            .fetch();

        return Optional.ofNullable(boardGames);
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
