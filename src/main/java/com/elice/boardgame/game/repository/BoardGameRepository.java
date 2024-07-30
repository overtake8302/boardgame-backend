package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardGameRepository extends JpaRepository<BoardGame, Long>,
    CustomBoardGameRepository {

    /*@Query("select count(gl) from GameLike gl where gl.boardGame.gameId = :gameId")
    int countLikesByBoardGameGameId(@Param("gameId") Long gameId);*/

    BoardGame findByGameIdAndDeletedDateIsNull(Long gameId);

    List<BoardGame> findByNameContaining(String keyword);
}
