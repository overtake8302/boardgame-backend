package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.LikeGenre;
import com.elice.boardgame.category.entity.QLikeGenre;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class CustomLikeGenreRepositoryImpl implements CustomLikeGenreRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<LikeGenre> findByUserIdOrderByScoreDesc(Long userId) {
        QLikeGenre likeGenre = QLikeGenre.likeGenre;
        return jpaQueryFactory.selectFrom(likeGenre)
            .where(likeGenre.user.id.eq(userId))
            .orderBy(likeGenre.score.desc())
            .fetch();
    }

    public Optional<LikeGenre> findByLikeGenreId(Long userId, Long genreId) {
        QLikeGenre likeGenre = QLikeGenre.likeGenre;
        LikeGenre result = jpaQueryFactory.selectFrom(likeGenre)
            .where(likeGenre.user.id.eq(userId)
                .and(likeGenre.genre.genreId.eq(genreId)))
            .fetchOne();
        return Optional.ofNullable(result);
    }
}
