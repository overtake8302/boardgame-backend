package com.elice.boardgame.auth.repository;

import static com.elice.boardgame.auth.entity.QUser.user;

import com.elice.boardgame.common.dto.SearchResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchResponse> searchUsersByKeyword(String keyword, Pageable pageable) {
        BooleanExpression predicate = user.username.containsIgnoreCase(keyword);

        List<SearchResponse> results = queryFactory.select(user)
            .from(user)
            .where(predicate)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch()
            .stream()
            .map(u -> new SearchResponse(u.getId(), u.getUsername()))
            .collect(Collectors.toList());

        long total = queryFactory.selectFrom(user)
            .where(predicate)
            .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
