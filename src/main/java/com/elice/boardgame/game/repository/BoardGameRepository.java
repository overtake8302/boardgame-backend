package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardGameRepository extends JpaRepository<BoardGame, Long>,
    CustomBoardGameRepository {

    /*@Query("select count(gl) from GameLike gl where gl.boardGame.gameId = :gameId")
    int countLikesByBoardGameGameId(@Param("gameId") Long gameId);*/

    BoardGame findByGameIdAndDeletedDateIsNull(Long gameId);

    List<BoardGame> findByNameContainingAndDeletedDateIsNull(String keyword);

    Page<BoardGame> findAllByDeletedDateIsNull(Pageable sortedPageable);

    @Query("SELECT bg FROM BoardGame bg LEFT JOIN bg.gameRates gr WHERE bg.deletedDate IS NULL GROUP BY bg ORDER BY AVG(gr.rate) DESC")
    Page<BoardGame> findAllOrderByAverageRateDesc(Pageable pageable);
}
