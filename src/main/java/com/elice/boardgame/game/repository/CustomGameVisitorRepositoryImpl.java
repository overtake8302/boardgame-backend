package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.entity.GameVisitor;
import com.elice.boardgame.game.entity.QBoardGame;
import com.elice.boardgame.game.entity.QGameVisitor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
        LocalDateTime createdAt = LocalDateTime.now();

        String sql = "INSERT IGNORE INTO gamevisitor (visitor_id, game_id, created_at) VALUES (:visitorId, :gameId, :createdAt)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("visitorId", visitorId);
        query.setParameter("gameId", gameId);
        query.setParameter("createdAt", createdAt); //native쿼리 작성시에는 create_at 자동 저장이 안되어서 변경했습니다.
        query.executeUpdate();
    }


    @Override
    public List<BoardGame> recentlyViewPosts(String visitorId) {
        QGameVisitor gameVisitor = QGameVisitor.gameVisitor;
        QBoardGame boardGame = QBoardGame.boardGame;

        List<BoardGame> boardGames = queryFactory
            .select(boardGame)
            .from(gameVisitor)
            .join(gameVisitor.boardGame, boardGame)
            .where(gameVisitor.id.visitorId.eq(visitorId))
            .orderBy(gameVisitor.createdAt.desc())
            .limit(6)
            .fetch();

        return boardGames;
    }


}
