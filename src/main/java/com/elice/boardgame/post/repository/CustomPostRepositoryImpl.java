package com.elice.boardgame.post.repository;

import static com.elice.boardgame.post.entity.QPost.post;

import com.elice.boardgame.common.enums.Enums.Category;
import com.elice.boardgame.post.dto.SearchPostResponse;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.entity.QPost;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> findAll(Pageable pageable, String sortBy) {
        QPost post = QPost.post;

        List<Post> posts = queryFactory
            .selectFrom(post)
            .orderBy(getSortOrder(sortBy))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(post)
            .fetchCount();

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<Post> findAllByType(Pageable pageable, String sortBy, String boardType) {
        QPost post = QPost.post;

        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(post.category.eq(boardType))
            .orderBy(getSortOrder(sortBy))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(post)
            .where(post.category.eq(boardType))
            .fetchCount();

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<Post> searchByQuery(Pageable pageable, String query) {
        QPost post = QPost.post;

        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(post.title.containsIgnoreCase(query)
                .or(post.content.containsIgnoreCase(query)
                    .or(post.user.username.containsIgnoreCase(query))))
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(post)
            .where(post.title.containsIgnoreCase(query)
                .or(post.content.containsIgnoreCase(query)))
            .fetchCount();

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<Post> searchByQuery(Pageable pageable, String query, String boardType) {
        QPost post = QPost.post;

        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(post.category.eq(boardType)
                .and(post.title.containsIgnoreCase(query)
                    .or(post.content.containsIgnoreCase(query)
                        .or(post.user.username.containsIgnoreCase(query)))))
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(post)
            .where(post.category.eq(boardType)
                .and(post.title.containsIgnoreCase(query)
                    .or(post.content.containsIgnoreCase(query))))
            .fetchCount();

        return new PageImpl<>(posts, pageable, total);
    }

    private OrderSpecifier<?> getSortOrder(String sortBy) {
        QPost post = QPost.post;

        switch (sortBy) {
            case "추천순":
                return post.likeCount.desc();
            case "조회순":
                return post.view.viewCount.desc();
            case "최신순":
            default:
                return post.createdAt.desc();
        }
    }

    @Override
    public Page<SearchPostResponse> searchPostsByKeyword(String keyword, Pageable pageable) {
        List<SearchPostResponse> results = queryFactory
            .select(
                Projections.constructor(SearchPostResponse.class,
                    post.id,
                    post.category,
                    post.title
                ))
            .from(post)
            .where(post.title.containsIgnoreCase(keyword))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(post)
            .where(post.title.containsIgnoreCase(keyword))
            .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
