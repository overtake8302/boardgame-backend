package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardGameRepository extends JpaRepository<BoardGame, Long>, CustomBoardGameRepository {


}
