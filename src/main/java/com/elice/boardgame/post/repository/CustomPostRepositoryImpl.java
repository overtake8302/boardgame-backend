package com.elice.boardgame.post.repository;

import com.elice.boardgame.common.enums.Enums.Category;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.entity.QPost;
import com.querydsl.core.types.OrderSpecifier;
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
    public Page<Post> findAllByType(Pageable pageable, String sortBy, String boardType) {
        QPost post = QPost.post;

        System.out.println(pageable.getOffset());
        System.out.println(pageable.getPageSize());

        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(post.category.eq(Category.valueOf(boardType)))
            .orderBy(getSortOrder(sortBy))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(post)
            .where(post.category.eq(Category.valueOf(boardType)))
            .fetchCount();

        System.out.println(posts.size());

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<Post> searchByQuery(Pageable pageable, String query, String boardType) {
        QPost post = QPost.post;

        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(post.category.eq(Category.valueOf(boardType))
                .and(post.title.containsIgnoreCase(query)
                    .or(post.content.containsIgnoreCase(query)
                        .or(post.user.username.containsIgnoreCase(query)))))
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(post)
            .where(post.category.eq(Category.valueOf(boardType))
                .and(post.title.containsIgnoreCase(query)
                    .or(post.content.containsIgnoreCase(query))))
            .fetchCount();

        return new PageImpl<>(posts, pageable, total);
    }

    private OrderSpecifier<?> getSortOrder(String sortBy) {
        QPost post = QPost.post;

        switch (sortBy) {
            case "추천순":
                return post.likes.desc();
            case "조회순":
                return post.view.viewCount.desc();
            case "최신순":
            default:
                return post.createdAt.desc();
        }
    }
}
