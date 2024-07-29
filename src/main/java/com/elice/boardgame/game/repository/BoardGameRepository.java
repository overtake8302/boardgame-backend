package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

<<<<<<< HEAD
public interface BoardGameRepository extends JpaRepository<BoardGame, Long>, BoardGameRepositoryCustom {
=======
import java.util.List;


public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {
>>>>>>> 4ed9a95655ad6c74f0815e51b00df7ffd7024a66

    /*@Query("select count(gl) from GameLike gl where gl.boardGame.gameId = :gameId")
    int countLikesByBoardGameGameId(@Param("gameId") Long gameId);*/

    BoardGame findByGameIdAndDeletedAtIsNull(Long gameId);

    List<BoardGame> findByNameContaining(String keyword);
}
