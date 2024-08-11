package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomGameLikeRepository {
    Page<BoardGame> findLikedGamesByUserId(Long userId, Pageable pageable);
}
