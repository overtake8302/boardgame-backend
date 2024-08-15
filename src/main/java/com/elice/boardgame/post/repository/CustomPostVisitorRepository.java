package com.elice.boardgame.post.repository;

import com.elice.boardgame.game.entity.BoardGame;

import java.util.List;

public interface CustomPostVisitorRepository {

    void insertIgnore(String visitorId, Long postId);
}
