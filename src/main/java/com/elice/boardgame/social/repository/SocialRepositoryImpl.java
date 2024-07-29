package com.elice.boardgame.social.repository;

import static com.elice.boardgame.social.entity.QSocial.social;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SocialRepositoryImpl implements SocialRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findFriendIdsByUserId(Long userId) {
        return queryFactory
            .select(social.id.friendId)
            .from(social)
            .where(social.id.userId.eq(userId))
            .fetch();
    }
}
