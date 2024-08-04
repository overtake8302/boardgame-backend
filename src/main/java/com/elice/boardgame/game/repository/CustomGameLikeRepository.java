package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;

public interface CustomGameLikeRepository {
    List<BoardGame> findLikedGamesByUserId(Long userId);
}
