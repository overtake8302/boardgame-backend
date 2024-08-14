package com.elice.boardgame.post.repository;

import com.elice.boardgame.auth.dto.MyCommentResponseDto;
import com.elice.boardgame.post.entity.QComment;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public Page<MyCommentResponseDto> findAllByUser_IdAndDeletedAtIsNull(Long userId, Pageable pageable) {

        QComment comment = QComment.comment;

        List<MyCommentResponseDto> results = queryFactory
                .select(Projections.bean(MyCommentResponseDto.class,
                        comment.id.as("id"),
                        comment.post.id.as("postId"),
                        comment.createdAt.stringValue().as("createdAt"),
                        comment.content.as("content")
                        ))
                .from(comment)
                .where(comment.user.id.eq(userId).and(comment.deletedAt.isNull()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(comment.id.desc())
                .fetch();

        long total = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.user.id.eq(userId).and(comment.deletedAt.isNull()))
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
