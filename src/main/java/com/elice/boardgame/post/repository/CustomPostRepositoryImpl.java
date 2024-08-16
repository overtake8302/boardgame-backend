package com.elice.boardgame.post.repository;

import static com.elice.boardgame.post.entity.QPost.post;

import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.game.dto.PostsByGame;
import com.elice.boardgame.game.entity.QBoardGame;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.dto.SearchPostResponse;
import com.elice.boardgame.post.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Page<Post> search(Pageable pageable, String query, String boardType, String sortBy) {
        QPost post = QPost.post;
        QPostVisitor postVisitor = QPostVisitor.postVisitor;
        QComment comment = QComment.comment;

        BooleanBuilder whereClause = new BooleanBuilder();

        whereClause.and(post.deletedAt.isNull());

        if (!"FULL".equals(boardType)) {
            whereClause.and(post.category.eq(boardType));
        }

        if (query != null && !query.isEmpty()) {
            whereClause.and(post.title.containsIgnoreCase(query)
                .or(post.user.username.containsIgnoreCase(query))
                .or(post.content.contains(query)));
        }

        List<Post> posts = queryFactory
                .select(Projections.fields(Post.class,
                        post.id.as("id"),
                        post.category.as("category"),
                        post.title.as("title"),
                        post.user.as("user"),
                        post.createdAt.as("createdAt"),
                        post.likeCount.as("likeCount"),
                    Expressions.as(
                            JPAExpressions
                                .select(postVisitor.id.countDistinct())
                                .from(postVisitor)
                                .where(postVisitor.post.id.eq(post.id)),
                        "view"
                            )

                ))
            .from(post)
            .where(whereClause)
            .orderBy(getSortOrder(sortBy))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .where(comment.post.id.in(posts.stream().map(Post::getId).collect(Collectors.toList()))
                        .and(comment.deletedAt.isNull()))
                .fetch();

        Map<Long, List<Comment>> commentsMap = comments.stream()
                .collect(Collectors.groupingBy(commentIn -> commentIn.getPost().getId()));

        posts.forEach(postIn -> {
            List<Comment> postComments = commentsMap.get(postIn.getId());
            if (postComments == null) {
                postComments = new ArrayList<>();
            }
            postIn.setComments(postComments);
        });


        long total = queryFactory
            .select(post.count())
            .from(post)
            .where(whereClause)
            .fetchOne();

        return new PageImpl<>(posts, pageable, total);
    }


    private OrderSpecifier<?> getSortOrder(String sortBy) {
        QPost post = QPost.post;

        switch (sortBy) {
            case "추천순":
                return post.likeCount.desc();
            case "조회순":
                return post.view.desc();
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
                    post.category, // category 필드 추가
                    post.title // name 필드
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

    @Override
    public List<PostsByGame> findTop10ByBoardGameGameIdAndCategoryAndDeletedAtIsNullOrderByIdDesc(Long gameId, String category) {

        QBoardGame boardGame = QBoardGame.boardGame;
        QPost post = QPost.post;

        List<PostsByGame> result = queryFactory
                .select(Projections.bean(PostsByGame.class,
                                post.id.as("postId"),
                                post.category.as("category"),
                                post.title.as("title"),
                                post.content.as("content"),
                                post.createdAt.stringValue().as("createdAt")
                        )
                        )
                .from(post)
                .where(post.boardGame.gameId.eq(gameId).and(post.category.eq(category)))
                .fetch();

        return result;
    }

    @Override
    public Post findPostById(Long id) {

        QPost post = QPost.post;
        QPostVisitor postVisitor = QPostVisitor.postVisitor;
        QComment comment = QComment.comment;

        Post result = queryFactory
                .select(Projections.fields(Post.class,
                        post.id.as("id"),
                        post.category.as("category"),
                        post.title.as("title"),
                        post.user.as("user"),
                        post.createdAt.as("createdAt"),
                        post.likeCount.as("likeCount"),
                        Expressions.as(
                                JPAExpressions
                                        .select(postVisitor.id.countDistinct())
                                        .from(postVisitor)
                                        .where(postVisitor.post.id.eq(post.id)),
                                "view"
                        ),
                        post.gameName.as("gameName"),
                        post.boardGame.as("boardGame"),
                        post.gameImageUrl.as("gameImageUrl"),
                        post.content.as("content")
                ))
                .from(post)
                .where(post.id.eq(id))
                .fetchOne();

        List<Comment> comments = queryFactory
                .selectFrom(QComment.comment)
                .where(QComment.comment.post.id.eq(id)
                        .and(QComment.comment.deletedAt.isNull()))
                .fetch();

        result.setComments(comments);

        return result;
    }
}
