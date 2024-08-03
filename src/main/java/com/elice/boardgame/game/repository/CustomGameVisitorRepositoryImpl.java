package com.elice.boardgame.game.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomGameVisitorRepositoryImpl implements CustomGameVisitorRepository{

    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory queryFactory;

    @Override
    public void insertIgnore(String visitorId, Long gameId) {
        String sql = "INSERT IGNORE INTO game_visitor (visitor_id, game_id) VALUES (:visitorId, :gameId)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("visitorId", visitorId);
        query.setParameter("gameId", gameId);
        query.executeUpdate();
    }
}
