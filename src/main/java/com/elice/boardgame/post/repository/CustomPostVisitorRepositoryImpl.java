package com.elice.boardgame.post.repository;

import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.QBoardGame;
import com.elice.boardgame.game.entity.QGameVisitor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomPostVisitorRepositoryImpl implements CustomPostVisitorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory queryFactory;

    @Override
    public void insertIgnore(String visitorId, Long postId) {
        LocalDateTime createdAt = LocalDateTime.now();

        String sql = "INSERT IGNORE INTO post_visitor (visitor_id, post_id, created_at) VALUES (:visitor_id, :post_id, :created_at)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("visitor_id", visitorId);
        query.setParameter("post_id", postId);
        query.setParameter("created_at", createdAt);
        query.executeUpdate();
    }


}
