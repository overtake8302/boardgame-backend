package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.BoardGame;
import java.util.List;

public interface CustomGameVisitorRepository {

    void insertIgnore(String visitorId, Long gameId);
    List<BoardGame> recentlyViewPosts(String visitorId);
}
