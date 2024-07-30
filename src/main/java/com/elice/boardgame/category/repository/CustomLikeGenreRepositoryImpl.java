package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.LikeGenre;
import com.elice.boardgame.category.entity.QLikeGenre;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
}
