package com.elice.boardgame.social.repository;

import static com.elice.boardgame.social.entity.QSocial.social;
import com.elice.boardgame.social.entity.Social;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SocialRepositoryImpl implements SocialRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public Page<Long> findFriendIdsByUserId(Long userId, Pageable pageable) {
        List<Long> friendIds = queryFactory
            .select(
                new CaseBuilder()
                    .when(social.id.userId.eq(userId)).then(social.id.friendId)
                    .otherwise(social.id.userId)
            )
            .from(social)
            .where(social.id.userId.eq(userId).or(social.id.friendId.eq(userId)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(social)
            .where(social.id.userId.eq(userId).or(social.id.friendId.eq(userId)))
            .fetchCount();

        return new PageImpl<>(friendIds, pageable, total);
    }

    @Override
    public boolean existsByUserIdAndFriendId(Long userId, Long friendId) {
        return queryFactory
            .selectFrom(social)
            .where((social.id.userId.eq(userId).and(social.id.friendId.eq(friendId)))
                .or(social.id.userId.eq(friendId).and(social.id.friendId.eq(userId))))
            .fetchCount() > 0;
    }

    @Override
    public void deleteByUserIdAndFriendIdBothWays(Long userId, Long friendId) {
        queryFactory
            .delete(social)
            .where((social.id.userId.eq(userId).and(social.id.friendId.eq(friendId)))
                .or(social.id.userId.eq(friendId).and(social.id.friendId.eq(userId))))
            .execute();
    }

    @Override
    @Transactional
    public Social save(Social social) {
        entityManager.persist(social);
        return social;
    }
}
